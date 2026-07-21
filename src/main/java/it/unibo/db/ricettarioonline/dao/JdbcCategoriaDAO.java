package it.unibo.db.ricettarioonline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.unibo.db.ricettarioonline.model.Categoria;

public class JdbcCategoriaDAO implements CategoriaDAO {

    private final Connection connection;

    public JdbcCategoriaDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public long insertOrUpdate(final Categoria categoria) throws SQLException {
        final String sql = "INSERT INTO CATEGORIE (Nome, Descrizione) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE Descrizione = VALUES(Descrizione)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescrizione());

            ps.executeUpdate();

            // Vale sia per il ramo INSERT (nuova categoria) sia per il ramo
            // UPDATE (categoria già esistente): MySQL restituisce comunque
            // il CodiceCategoria della riga coinvolta.
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento/aggiornamento categoria fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public List<Categoria> findAll() throws SQLException {
        final String sql = "SELECT CodiceCategoria, Nome, Descrizione FROM CATEGORIE ORDER BY Nome";
        final List<Categoria> risultato = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risultato.add(new Categoria(
                        rs.getLong("CodiceCategoria"),
                        rs.getString("Nome"),
                        rs.getString("Descrizione")
                ));
            }
        }

        return risultato;
    }
}
