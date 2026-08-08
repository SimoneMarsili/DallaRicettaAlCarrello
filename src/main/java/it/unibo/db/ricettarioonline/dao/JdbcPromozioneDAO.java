package it.unibo.db.ricettarioonline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import it.unibo.db.ricettarioonline.model.Promozione;

public class JdbcPromozioneDAO implements PromozioneDAO {

    private final Connection connection;

    public JdbcPromozioneDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public long insert(final Promozione promozione) throws SQLException {
        final String sql = "INSERT INTO PROMOZIONI (Nome, DataInizio, DataFine) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, promozione.getNome());
            ps.setObject(2, promozione.getDataInizio());
            ps.setObject(3, promozione.getDataFine());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento promozione fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public List<Promozione> findAll() throws SQLException {
        final String sql = "SELECT CodicePromo, Nome, DataInizio, DataFine FROM PROMOZIONI ORDER BY DataInizio DESC";
        final List<Promozione> risultato = new java.util.ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                risultato.add(new Promozione(
                        rs.getLong("CodicePromo"),
                        rs.getString("Nome"),
                        rs.getObject("DataInizio", LocalDate.class),
                        rs.getObject("DataFine", LocalDate.class)
                ));
            }
        }
        return risultato;
    }
}