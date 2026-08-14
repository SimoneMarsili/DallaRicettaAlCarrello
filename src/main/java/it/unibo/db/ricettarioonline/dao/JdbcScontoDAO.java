package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Sconto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public BigDecimal findMigliorSconto(final long codiceRicetta) throws SQLException {
        final String sql = "SELECT COALESCE(MAX(S.PercentualeSconto), 0) "
                + "FROM RICETTE R "
                + "JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta "
                + "JOIN SCONTI S "
                + "    ON S.CodiceCategoria = CL.CodiceCategoria "
                + "   AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti "
                + "JOIN PROMOZIONI P "
                + "    ON P.CodicePromo = S.CodicePromo "
                + "WHERE R.CodiceRicetta = ? "
                + "AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codiceRicetta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
                return BigDecimal.ZERO;
            }
        }
    }
}