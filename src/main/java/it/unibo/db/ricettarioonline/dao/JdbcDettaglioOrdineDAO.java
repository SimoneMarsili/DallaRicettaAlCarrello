package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.DettaglioOrdine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcDettaglioOrdineDAO implements DettaglioOrdineDAO {

    private final Connection connection;

    public JdbcDettaglioOrdineDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(final DettaglioOrdine dettaglioOrdine) throws SQLException {
        final String sql = "INSERT INTO DETTAGLI_ORDINE "
                + "(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, dettaglioOrdine.getCodiceOrdine());
            ps.setLong(2, dettaglioOrdine.getCodiceRicetta());
            ps.setBigDecimal(3, dettaglioOrdine.getPrezzoUnitario());
            ps.setInt(4, dettaglioOrdine.getQuantita());
            ps.setBigDecimal(5, dettaglioOrdine.getScontoApplicato());

            ps.executeUpdate();
        }
    }
}