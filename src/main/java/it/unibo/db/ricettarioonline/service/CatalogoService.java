package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.dao.IngredienteDAO;
import it.unibo.db.ricettarioonline.dao.JdbcIngredienteDAO;
import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.VantaggioAttivo;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CatalogoService {

    // U4 - Visualizzazione ingredienti. Passa attraverso IngredienteDAO:
    // è una lettura semplice su una singola tabella, legittimamente nel DAO.
    public List<Ingrediente> elencaIngredienti() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final IngredienteDAO ingredienteDAO = new JdbcIngredienteDAO(connection);
            return ingredienteDAO.findAll();
        }
    }

    // U7 - Visualizzazione vantaggi/promozioni attualmente attivi.
    // SQL diretto qui nel service, non in un DAO: la query unisce
    // SCONTI+PROMOZIONI+CATEGORIE, non è legata al ciclo di vita di una
    // singola entità (coerente con la policy adottata per i report/JOIN
    // multi-tabella, vista in SAGE per AdminService).
    public List<VantaggioAttivo> vantaggiAttivi() throws SQLException {
        final String sql = "SELECT "
                + "P.Nome AS NomePromo, P.DataInizio, P.DataFine, "
                + "C.Nome AS NomeCategoria, "
                + "S.MinIngredienti, S.MaxIngredienti, S.PercentualeSconto "
                + "FROM SCONTI S "
                + "JOIN PROMOZIONI P ON P.CodicePromo = S.CodicePromo "
                + "JOIN CATEGORIE C ON C.CodiceCategoria = S.CodiceCategoria "
                + "WHERE CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine "
                + "ORDER BY S.PercentualeSconto DESC";

        final List<VantaggioAttivo> risultato = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risultato.add(new VantaggioAttivo(
                        rs.getString("NomePromo"),
                        rs.getObject("DataInizio", LocalDate.class),
                        rs.getObject("DataFine", LocalDate.class),
                        rs.getString("NomeCategoria"),
                        rs.getInt("MinIngredienti"),
                        rs.getInt("MaxIngredienti"),
                        rs.getBigDecimal("PercentualeSconto")
                ));
            }
        }

        return risultato;
    }
}