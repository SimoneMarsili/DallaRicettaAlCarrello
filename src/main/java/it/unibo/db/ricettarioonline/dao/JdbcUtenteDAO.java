package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcUtenteDAO implements UtenteDAO {

    private final Connection connection;

    public JdbcUtenteDAO(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public long registra(final String nome, final String cognome, final String email,
            final String passwordChiara, final String indirizzoSpedizione) throws SQLException {

        final String sql = "INSERT INTO UTENTI "
                + "(Nome, Cognome, Email, Password, Ruolo, Attivo, IndirizzoSpedizione) "
                + "VALUES (?, ?, ?, SHA2(?, 512), 'UTENTE', TRUE, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, passwordChiara); // MySQL calcola SHA2(...) al volo, mai in chiaro nel DB
            ps.setString(5, indirizzoSpedizione);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Inserimento utente fallito: nessuna chiave generata");
            }
        }
    }

    @Override
    public Optional<Utente> login(final String email, final String passwordChiara) throws SQLException {
        final String sql = "SELECT CodiceUtente, Nome, Cognome, Email, Password, Ruolo, "
                + "Attivo, IndirizzoSpedizione "
                + "FROM UTENTI WHERE Email = ? AND Password = SHA2(?, 512)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordChiara);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUtente(rs));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void disattivaBatch(final List<Long> codiciUtente) throws SQLException {
        if (codiciUtente.isEmpty()) {
            return;
        }

        final String placeholders = codiciUtente.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        final String sql = "UPDATE UTENTI SET Attivo = FALSE "
                + "WHERE CodiceUtente IN (" + placeholders + ")";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < codiciUtente.size(); i++) {
                ps.setLong(i + 1, codiciUtente.get(i));
            }
            ps.executeUpdate();
        }
    }

    private Utente mapUtente(final ResultSet rs) throws SQLException {
        return new Utente(
                rs.getLong("CodiceUtente"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email"),
                rs.getString("Password"),
                rs.getString("Ruolo"),
                rs.getBoolean("Attivo"),
                rs.getString("IndirizzoSpedizione")
        );
    }
}