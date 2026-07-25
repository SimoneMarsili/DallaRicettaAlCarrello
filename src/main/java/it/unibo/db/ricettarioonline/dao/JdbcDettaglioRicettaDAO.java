package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.DettaglioRicetta;
import it.unibo.db.ricettarioonline.model.IngredienteRicetta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcDettaglioRicettaDAO implements DettaglioRicettaDAO {

    private final Connection connection;

    // La Connection arriva sempre e solo dal costruttore. Per un'operazione
    // transazionale (es. U6), il service creerà una nuova istanza di questo
    // DAO passando la Connection della transazione in corso.
    public JdbcDettaglioRicettaDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(final DettaglioRicetta dettaglioRicetta) throws SQLException {
        final String sql = "INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, dettaglioRicetta.getCodiceRicetta());
            ps.setLong(2, dettaglioRicetta.getCodiceIngrediente());
            ps.setBigDecimal(3, dettaglioRicetta.getQuantita());

            ps.executeUpdate();
        }
    }

    @Override
    public List<IngredienteRicetta> findByRicetta(final long codiceRicetta) throws SQLException {
        final String sql = "SELECT I.CodiceIngrediente, I.Nome, DR.Quantità "
                + "FROM DETTAGLI_RICETTA DR "
                + "JOIN INGREDIENTI I ON I.CodiceIngrediente = DR.CodiceIngrediente "
                + "WHERE DR.CodiceRicetta = ?";

        final List<IngredienteRicetta> risultato = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codiceRicetta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new IngredienteRicetta(
                            rs.getLong("CodiceIngrediente"),
                            rs.getString("Nome"),
                            rs.getBigDecimal("Quantità")
                    ));
                }
            }
        }

        return risultato;
    }
}