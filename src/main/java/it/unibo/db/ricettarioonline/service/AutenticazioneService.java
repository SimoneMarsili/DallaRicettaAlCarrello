package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.dao.JdbcUtenteDAO;
import it.unibo.db.ricettarioonline.dao.UtenteDAO;
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
    // Nota: per ora non controlla il campo Attivo — lo aggiungeremo quando
    // affronteremo il flusso di login/gestione errori per intero.
    public Optional<Utente> login(final String email, final String passwordChiara) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final UtenteDAO utenteDAO = new JdbcUtenteDAO(connection);
            return utenteDAO.login(email, passwordChiara);
        }
    }
}