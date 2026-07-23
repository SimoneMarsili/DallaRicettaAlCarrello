package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.model.UtenteConRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcUtenteDAO implements UtenteDAO {

    private final Connection connection;

    public JdbcUtenteDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public long insert(final Utente utente) throws SQLException {
        final String sql = "INSERT INTO UTENTI "
                + "(Nome, Cognome, Email, Password, Ruolo, Attivo, IndirizzoSpedizione) "
                + "VALUES (?, ?, ?, ?, 'UTENTE', TRUE, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getPassword());
            ps.setString(5, utente.getIndirizzoSpedizione());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento utente fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public Optional<Utente> findByEmail(final String email) throws SQLException {
        final String sql = "SELECT CodiceUtente, Nome, Cognome, Email, Password, Ruolo, "
                + "Attivo, IndirizzoSpedizione "
                + "FROM UTENTI WHERE Email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUtente(rs));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<UtenteConRating> findMigliori(final int limit) throws SQLException {
        final String sql = "SELECT "
                + "U.CodiceUtente, U.Nome, U.Cognome, "
                + "AVG(R.MediaRecensioni) AS RatingUtente, "
                + "COUNT(R.CodiceRicetta) AS NumeroRicette "
                + "FROM UTENTI U "
                + "JOIN RICETTE R ON R.CodiceUtente = U.CodiceUtente "
                + "WHERE R.Rimossa = FALSE AND U.Attivo = TRUE "
                + "GROUP BY U.CodiceUtente, U.Nome, U.Cognome "
                + "ORDER BY RatingUtente DESC "
                + "LIMIT ?";

        final List<UtenteConRating> risultato = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new UtenteConRating(
                            rs.getLong("CodiceUtente"),
                            rs.getString("Nome"),
                            rs.getString("Cognome"),
                            rs.getBigDecimal("RatingUtente"),
                            rs.getInt("NumeroRicette")
                    ));
                }
            }
        }

        return risultato;
    }

    @Override
    public void disattivaBatch(final Connection conn, final List<Long> codiciUtente) throws SQLException {
        if (codiciUtente.isEmpty()) {
            return; // niente da fare, evitiamo una IN () invalida
        }

        // Costruiamo dinamicamente i placeholder "?, ?, ?, ..." in base alla
        // dimensione della lista, perché JDBC non supporta direttamente il
        // passaggio di una List come singolo parametro di un IN (...).
        final String placeholders = codiciUtente.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        final String sql = "UPDATE UTENTI SET Attivo = FALSE "
                + "WHERE CodiceUtente IN (" + placeholders + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < codiciUtente.size(); i++) {
                ps.setLong(i + 1, codiciUtente.get(i));
            }
            ps.executeUpdate();
        }
    }

    // Metodo privato di mapping: evita di duplicare la stessa logica di
    // costruzione dell'oggetto Utente se in futuro aggiungessimo altri metodi
    // di lettura (es. findById).
    private Utente mapUtente(final ResultSet rs) throws SQLException {
        return new Utente(
                rs.getLong("CodiceUtente"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email"),
                rs.getString("Password"),
                rs.getString("Ruolo"),
                rs.getBoolean("Attivo"),
                rs.getString("IndirizzoSpedizione")
        );
    }
}
