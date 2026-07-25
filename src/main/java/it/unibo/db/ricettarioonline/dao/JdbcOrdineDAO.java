package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Ordine;
import it.unibo.db.ricettarioonline.model.RigaStoricoOrdine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrdineDAO implements OrdineDAO {

    private final Connection connection;

    public JdbcOrdineDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public long insert(final Ordine ordine) throws SQLException {
        final String sql = "INSERT INTO ORDINI (Data, Note, CodiceUtente) VALUES (CURRENT_DATE, ?, ?)";

        // Il trigger trg_ordini_verifica_indirizzo, lato database, blocca questo
        // INSERT se l'utente non ha un IndirizzoSpedizione impostato: in quel caso
        // executeUpdate lancerà una SQLException con il messaggio del SIGNAL.
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ordine.getNote());
            ps.setLong(2, ordine.getCodiceUtente());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento ordine fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public List<RigaStoricoOrdine> findStoricoByUtente(final long codiceUtente) throws SQLException {
        final String sql = "SELECT "
                + "O.CodiceOrdine, O.Data, R.CodiceRicetta, R.Nome AS NomeRicetta, "
                + "DEO.Quantità, DEO.PrezzoUnitario, DEO.ScontoApplicato, "
                + "ROUND(DEO.Quantità * DEO.PrezzoUnitario * (1 - DEO.ScontoApplicato / 100), 2) AS TotaleRiga "
                + "FROM ORDINI O "
                + "JOIN DETTAGLI_ORDINE DEO ON O.CodiceOrdine = DEO.CodiceOrdine "
                + "JOIN RICETTE R ON DEO.CodiceRicetta = R.CodiceRicetta "
                + "WHERE O.CodiceUtente = ? "
                + "ORDER BY O.Data DESC, O.CodiceOrdine";

        final List<RigaStoricoOrdine> risultato = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codiceUtente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new RigaStoricoOrdine(
                            rs.getLong("CodiceOrdine"),
                            rs.getObject("Data", LocalDate.class),
                            rs.getLong("CodiceRicetta"),
                            rs.getString("NomeRicetta"),
                            rs.getInt("Quantità"),
                            rs.getBigDecimal("PrezzoUnitario"),
                            rs.getBigDecimal("ScontoApplicato"),
                            rs.getBigDecimal("TotaleRiga")
                    ));
                }
            }
        }

        return risultato;
    }
}
