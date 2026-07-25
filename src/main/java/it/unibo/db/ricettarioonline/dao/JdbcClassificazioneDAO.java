package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.Classificazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcClassificazioneDAO implements ClassificazioneDAO {

    private final Connection connection;

    public JdbcClassificazioneDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(final Classificazione classificazione) throws SQLException {
        final String sql = "INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, classificazione.getCodiceRicetta());
            ps.setLong(2, classificazione.getCodiceCategoria());

            ps.executeUpdate();
        }
    }

    @Override
    public List<Categoria> findByRicetta(final long codiceRicetta) throws SQLException {
        final String sql = "SELECT C.CodiceCategoria, C.Nome, C.Descrizione "
                + "FROM CLASSIFICAZIONI CL "
                + "JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria "
                + "WHERE CL.CodiceRicetta = ?";

        final List<Categoria> risultato = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codiceRicetta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new Categoria(
                            rs.getLong("CodiceCategoria"),
                            rs.getString("Nome"),
                            rs.getString("Descrizione")
                    ));
                }
            }
        }

        return risultato;
    }
}