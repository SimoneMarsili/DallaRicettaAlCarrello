package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Recensione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcRecensioneDAO implements RecensioneDAO {

    private final Connection connection;

    public JdbcRecensioneDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(final Recensione recensione) throws SQLException {
        final String sql = "INSERT INTO RECENSIONI "
                + "(CodiceUtente, CodiceRicetta, Data, Voto, Commento) "
                + "VALUES (?, ?, CURRENT_DATE, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, recensione.getCodiceUtente());
            ps.setLong(2, recensione.getCodiceRicetta());
            ps.setInt(3, recensione.getVoto());
            ps.setString(4, recensione.getCommento());

            ps.executeUpdate();
        }
    }
}