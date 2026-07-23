package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Sconto;
import it.unibo.db.ricettarioonline.model.VantaggioAttivo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcScontoDAO implements ScontoDAO {

    private final Connection connection;

    // La Connection arriva dal chiamante e viene usata per insertOrUpdate/findAttivi.
    // findMigliorSconto invece riceve una Connection esplicita come parametro,
    // perché deve condividere la transazione dell'ordine in corso (U9) e non
    // necessariamente quella di questa istanza del DAO.
    public JdbcScontoDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertOrUpdate(final Sconto sconto) throws SQLException {
        final String sql = "INSERT INTO SCONTI "
                + "(CodicePromo, CodiceCategoria, MinIngredienti, MaxIngredienti, PercentualeSconto) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE PercentualeSconto = VALUES(PercentualeSconto)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, sconto.getCodicePromo());
            ps.setLong(2, sconto.getCodiceCategoria());
            ps.setInt(3, sconto.getMinIngredienti());
            ps.setInt(4, sconto.getMaxIngredienti());
            ps.setBigDecimal(5, sconto.getPercentualeSconto());

            ps.executeUpdate();
        }
    }

    @Override
    public BigDecimal findMigliorSconto(final Connection conn, final long codiceRicetta) throws SQLException {
        final String sql = "SELECT COALESCE(MAX(S.PercentualeSconto), 0) "
                + "FROM RICETTE R "
                + "LEFT JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta "
                + "LEFT JOIN SCONTI S "
                + "    ON S.CodiceCategoria = CL.CodiceCategoria "
                + "   AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti "
                + "LEFT JOIN PROMOZIONI P "
                + "    ON P.CodicePromo = S.CodicePromo "
                + "   AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine "
                + "WHERE R.CodiceRicetta = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, codiceRicetta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
                return BigDecimal.ZERO;
            }
        }
    }

    @Override
    public List<VantaggioAttivo> findAttivi() throws SQLException {
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

        try (PreparedStatement ps = connection.prepareStatement(sql);
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