package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.dao.JdbcUtenteDAO;
import it.unibo.db.ricettarioonline.dao.UtenteDAO;
import it.unibo.db.ricettarioonline.exception.AccountDisattivatoException;
import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class AutenticazioneService {

    // U1 - Registrazione utente.
    public long registraUtente(final String nome, final String cognome, final String email,
            final String passwordChiara, final String indirizzoSpedizione) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final UtenteDAO utenteDAO = new JdbcUtenteDAO(connection);
            return utenteDAO.registra(nome, cognome, email, passwordChiara, indirizzoSpedizione);
        }
    }

    // Login: ricerca + verifica password in un solo passaggio (fatto lato SQL).
    // Se le credenziali sono corrette ma l'account è disattivato, lancia
    // AccountDisattivatoException invece di lasciar passare l'accesso o
    // confonderlo con "credenziali errate" (Optional.empty()).
    public Optional<Utente> login(final String email, final String passwordChiara)
            throws SQLException, AccountDisattivatoException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final UtenteDAO utenteDAO = new JdbcUtenteDAO(connection);
            final Optional<Utente> utente = utenteDAO.login(email, passwordChiara);

            if (utente.isPresent() && !utente.get().isAttivo()) {
                throw new AccountDisattivatoException(
                        "Questo account è stato disattivato. Contatta l'assistenza.");
            }

            return utente;
        }
    }
}