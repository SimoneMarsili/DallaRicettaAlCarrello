package it.unibo.db.ricettarioonline.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import it.unibo.db.ricettarioonline.model.Ingrediente;

public class JdbcIngredienteDAO implements IngredienteDAO {

    private final Connection connection;

    // La Connection arriva dal chiamante (service layer), non viene aperta qui.
    // Questo è ciò che permette a più DAO di condividere la stessa transazione.
    public JdbcIngredienteDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public long insert(final Ingrediente ingrediente) throws SQLException {
        final String sql = "INSERT INTO INGREDIENTI (Nome, Prezzo, Vegano) VALUES (?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS dice al driver JDBC di tenere traccia
        // della chiave AUTO_INCREMENT generata da questo INSERT, cosi' da poterla
        // recuperare subito dopo con getGeneratedKeys().
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ingrediente.getNome());
            ps.setBigDecimal(2, ingrediente.getPrezzo());
            ps.setBoolean(3, ingrediente.isVegano());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento ingrediente fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public void update(final long codiceIngrediente, final String nome,
                        final BigDecimal prezzo, final Boolean vegano) throws SQLException {
        final String sql = "UPDATE INGREDIENTI "
                + "SET Nome = COALESCE(?, Nome), Prezzo = COALESCE(?, Prezzo), Vegano = COALESCE(?, Vegano) "
                + "WHERE CodiceIngrediente = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // setString e setBigDecimal accettano null: il driver scrive NULL a livello SQL, e COALESCE fa il resto.
            ps.setString(1, nome);
            ps.setBigDecimal(2, prezzo);

            // Boolean e' un tipo primitivo lato SQL/JDBC: setBoolean(int, boolean)
            // NON accetta null, quindi per rappresentare "non aggiornare questo campo"
            // dobbiamo chiamare esplicitamente setNull con il tipo corretto.
            if (vegano == null) {
                ps.setNull(3, Types.BOOLEAN);
            } else {
                ps.setBoolean(3, vegano);
            }

            ps.setLong(4, codiceIngrediente);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Ingrediente> findAll() throws SQLException {
    final String sql = "SELECT CodiceIngrediente, Nome, Prezzo, Vegano FROM INGREDIENTI ORDER BY Nome";
    final List<Ingrediente> risultato = new ArrayList<>();

    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            risultato.add(new Ingrediente(
                    rs.getLong("CodiceIngrediente"),
                    rs.getString("Nome"),
                    rs.getBigDecimal("Prezzo"),
                    rs.getBoolean("Vegano")
            ));
        }
    }

    return risultato;
}
}
