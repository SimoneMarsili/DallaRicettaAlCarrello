package it.unibo.db.ricettarioonline.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unibo.db.ricettarioonline.model.Ricetta;
import it.unibo.db.ricettarioonline.model.RicettaVista;

public class JdbcRicettaDAO implements RicettaDAO {

    // Parte comune a tutte le query di visualizzazione: cambia solo la WHERE
    // (e a volte i JOIN aggiuntivi) a seconda del filtro applicato.
    private static final String SELECT_BASE =
            "SELECT R.CodiceRicetta, R.Nome AS NomeRicetta, R.Preparazione, R.TempoRichiesto, "
            + "R.PrezzoRicetta, R.MediaRecensioni, U.Nome AS NomeAutore, U.Cognome AS CognomeAutore "
            + "FROM RICETTE R "
            + "JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente ";

    private final Connection connection;

    public JdbcRicettaDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
        public long insert(final Ricetta ricetta) throws SQLException {
        final String sql = "INSERT INTO RICETTE "
                + "(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta) "
                + "VALUES (?, ?, ?, ?, 0, 0)";

        // NumeroIngredienti e PrezzoRicetta partono sempre da 0 qui: li
        // aggiorneranno automaticamente i trigger quando, subito dopo,
        // il service inserirà le righe di DETTAGLI_RICETTA (stessa transazione).
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ricetta.getNome());
            ps.setLong(2, ricetta.getCodiceUtente());
            ps.setString(3, ricetta.getPreparazione());
            ps.setInt(4, ricetta.getTempoRichiesto());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento ricetta fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public Optional<RicettaVista> findById(final long codiceRicetta) throws SQLException {
        final String sql = SELECT_BASE + "WHERE R.CodiceRicetta = ? AND R.Rimossa = FALSE";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codiceRicetta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRicettaVista(rs));
                }
                return Optional.empty();
            }
        }
    }

    // Mapping condiviso da tutti i metodi che restituiscono una lista:
    // itera un ResultSet già aperto e lo trasforma in List<RicettaVista>.
    private List<RicettaVista> mapLista(final ResultSet rs) throws SQLException {
        final List<RicettaVista> risultato = new ArrayList<>();
        while (rs.next()) {
            risultato.add(mapRicettaVista(rs));
        }
        return risultato;
    }

    // Mapping condiviso: lo riusiamo anche nelle findByXxx del prossimo giro.
    private RicettaVista mapRicettaVista(final ResultSet rs) throws SQLException {
        return new RicettaVista(
                rs.getLong("CodiceRicetta"),
                rs.getString("NomeRicetta"),
                rs.getString("Preparazione"),
                rs.getInt("TempoRichiesto"),
                rs.getBigDecimal("PrezzoRicetta"),
                rs.getBigDecimal("MediaRecensioni"),
                rs.getString("NomeAutore"),
                rs.getString("CognomeAutore")
        );
    }

   @Override
    public List<RicettaVista> findVisibili() throws SQLException {
        final String sql = SELECT_BASE + "WHERE R.Rimossa = FALSE ORDER BY R.MediaRecensioni DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapLista(rs);
        }
    }

    @Override
    public List<RicettaVista> findByNome(final String nome) throws SQLException {
        final String sql = SELECT_BASE + "WHERE R.Rimossa = FALSE AND R.Nome = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                return mapLista(rs);
            }
        }
    }

    @Override
    public List<RicettaVista> findByIngrediente(final String nomeIngrediente) throws SQLException {
        final String sql = SELECT_BASE
                + "JOIN DETTAGLI_RICETTA DR ON R.CodiceRicetta = DR.CodiceRicetta "
                + "JOIN INGREDIENTI I ON DR.CodiceIngrediente = I.CodiceIngrediente "
                + "WHERE R.Rimossa = FALSE AND I.Nome = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomeIngrediente);
            try (ResultSet rs = ps.executeQuery()) {
                return mapLista(rs);
            }
        }
    }

    @Override
    public List<RicettaVista> findByTempoMax(final int minuti) throws SQLException {
        final String sql = SELECT_BASE + "WHERE R.Rimossa = FALSE AND R.TempoRichiesto <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, minuti);
            try (ResultSet rs = ps.executeQuery()) {
                return mapLista(rs);
            }
        }
    }

    @Override
    public List<RicettaVista> findByPrezzoRange(final BigDecimal min, final BigDecimal max) throws SQLException {
        final String sql = SELECT_BASE + "WHERE R.Rimossa = FALSE AND R.PrezzoRicetta BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                return mapLista(rs);
            }
        }
    }

    @Override
    public List<RicettaVista> findByAutore(final String nome, final String cognome) throws SQLException {
        final String sql = SELECT_BASE + "WHERE R.Rimossa = FALSE AND U.Nome = ? AND U.Cognome = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, cognome);
            try (ResultSet rs = ps.executeQuery()) {
                return mapLista(rs);
            }
        }
    }

    @Override
    public List<RicettaVista> findByCategoria(final String nomeCategoria) throws SQLException {
        final String sql = SELECT_BASE
                + "JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta "
                + "JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria "
                + "WHERE R.Rimossa = FALSE AND C.Nome = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomeCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                return mapLista(rs);
            }
        }
    }

    @Override
    public void rimuoviBatch(final List<Long> codiciUtente) throws SQLException {
        if (codiciUtente.isEmpty()) {
            return; // niente da fare, evitiamo una IN () invalida
        }

        final String placeholders = codiciUtente.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        final String sql = "UPDATE RICETTE SET Rimossa = TRUE "
                + "WHERE CodiceUtente IN (" + placeholders + ")";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < codiciUtente.size(); i++) {
                ps.setLong(i + 1, codiciUtente.get(i));
            }
            ps.executeUpdate();
        }
    }

}
