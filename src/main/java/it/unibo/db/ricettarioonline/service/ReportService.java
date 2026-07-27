package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.CategoriaOrdinata;
import it.unibo.db.ricettarioonline.model.RicettaOrdinata;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.model.UtenteConRating;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    // U8.1 - Migliori ricette, ordinate per MediaRecensioni. Nessuna
    // aggregazione reale (niente GROUP BY): il risultato coincide con
    // RicettaVista, già usata da RicettaDAO/RicettaService per U2/U3.x.
    public List<RicettaVista> migliriRicette(final int limit) throws SQLException {
        final String sql = "SELECT "
                + "R.CodiceRicetta, R.Nome AS NomeRicetta, R.Preparazione, R.TempoRichiesto, "
                + "R.PrezzoRicetta, R.MediaRecensioni, U.Nome AS NomeAutore, U.Cognome AS CognomeAutore "
                + "FROM RICETTE R "
                + "JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente "
                + "WHERE R.Rimossa = FALSE "
                + "ORDER BY R.MediaRecensioni DESC "
                + "LIMIT ?";

        final List<RicettaVista> risultato = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new RicettaVista(
                            rs.getLong("CodiceRicetta"),
                            rs.getString("NomeRicetta"),
                            rs.getString("Preparazione"),
                            rs.getInt("TempoRichiesto"),
                            rs.getBigDecimal("PrezzoRicetta"),
                            rs.getBigDecimal("MediaRecensioni"),
                            rs.getString("NomeAutore"),
                            rs.getString("CognomeAutore")
                    ));
                }
            }
        }

        return risultato;
    }

    // U8.2 - Migliori utenti, ordinati per rating medio delle proprie ricette.
    public List<UtenteConRating> miglioriUtenti(final int limit) throws SQLException {
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

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

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

    // U8.3 - Ricette più ordinate, per quantità totale ordinata.
    public List<RicettaOrdinata> ricettePiuOrdinate(final int limit) throws SQLException {
        final String sql = "SELECT "
                + "R.CodiceRicetta, R.Nome AS NomeRicetta, R.PrezzoRicetta, "
                + "SUM(DEO.Quantità) AS QuantitaTotaleOrdinata "
                + "FROM DETTAGLI_ORDINE DEO "
                + "JOIN RICETTE R ON R.CodiceRicetta = DEO.CodiceRicetta "
                + "WHERE R.Rimossa = FALSE "
                + "GROUP BY R.CodiceRicetta, R.Nome, R.PrezzoRicetta "
                + "ORDER BY QuantitaTotaleOrdinata DESC "
                + "LIMIT ?";

        final List<RicettaOrdinata> risultato = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new RicettaOrdinata(
                            rs.getLong("CodiceRicetta"),
                            rs.getString("NomeRicetta"),
                            rs.getBigDecimal("PrezzoRicetta"),
                            rs.getLong("QuantitaTotaleOrdinata")
                    ));
                }
            }
        }

        return risultato;
    }

    // U8.4 - Categorie più ordinate, per quantità totale ordinata.
    public List<CategoriaOrdinata> categoriePiuOrdinate(final int limit) throws SQLException {
        final String sql = "SELECT "
                + "C.CodiceCategoria, C.Nome AS NomeCategoria, "
                + "SUM(DO.Quantità) AS QuantitaTotaleOrdinata "
                + "FROM DETTAGLI_ORDINE DO "
                + "JOIN RICETTE R ON R.CodiceRicetta = DO.CodiceRicetta "
                + "JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta "
                + "JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria "
                + "GROUP BY C.CodiceCategoria, C.Nome "
                + "ORDER BY QuantitaTotaleOrdinata DESC "
                + "LIMIT ?";

        final List<CategoriaOrdinata> risultato = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(new CategoriaOrdinata(
                            rs.getLong("CodiceCategoria"),
                            rs.getString("NomeCategoria"),
                            rs.getLong("QuantitaTotaleOrdinata")
                    ));
                }
            }
        }

        return risultato;
    }
}
