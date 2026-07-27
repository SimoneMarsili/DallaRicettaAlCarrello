package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutenticazioneServiceTest {

    private final AutenticazioneService autenticazioneService = new AutenticazioneService();

    // Tiene traccia degli utenti creati durante il test corrente, per poterli
    // ripulire in automatico dopo ogni test (vedi pulisciUtentiDiTest sotto).
    private final List<Long> codiciUtenteDaRipulire = new ArrayList<>();

    // Gira dopo OGNI singolo test (passato o fallito): elimina fisicamente
    // gli utenti creati durante il test, così il DB torna come prima.
    // DELETE FROM UTENTI non esiste in nessun DAO/service applicativo (non
    // serve alla specifica, solo alla pulizia dei test): resta locale qui.
    @AfterEach
    void pulisciUtentiDiTest() throws Exception {
        if (codiciUtenteDaRipulire.isEmpty()) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM UTENTI WHERE CodiceUtente = ?")) {

            for (final Long codiceUtente : codiciUtenteDaRipulire) {
                ps.setLong(1, codiceUtente);
                ps.executeUpdate();
            }
        }

        codiciUtenteDaRipulire.clear();
    }

    // U1 + login - Test "a giro completo": registra un utente nuovo, poi
    // verifica che il login con la STESSA password in chiaro funzioni.
    @Test
    void registrazioneSeguitaDaLoginFunziona() throws Exception {
        final String email = "test.registrazione." + System.currentTimeMillis() + "@example.com";
        final String passwordChiara = "PasswordDiProva123";

        final long codiceUtente = autenticazioneService.registraUtente(
                "Mario", "Rossi", email, passwordChiara, "Via di Prova 1, Bologna");
        codiciUtenteDaRipulire.add(codiceUtente); // verrà eliminato da pulisciUtentiDiTest

        assertTrue(codiceUtente > 0, "Il codice utente generato deve essere positivo");

        final Optional<Utente> utenteLoggato = autenticazioneService.login(email, passwordChiara);

        assertTrue(utenteLoggato.isPresent(), "Il login con la password appena impostata deve riuscire");
        assertEquals("Mario", utenteLoggato.get().getNome());
        assertEquals("Rossi", utenteLoggato.get().getCognome());
        assertEquals(codiceUtente, utenteLoggato.get().getCodiceUtente());
    }

    // Login - Con i dati demo dello script di popolamento: nessuna scrittura,
    // quindi nessuna pulizia necessaria per questo test.
    @Test
    void loginConDatiDemoFunziona() throws Exception {
        final Optional<Utente> utente = autenticazioneService.login("utente1@mail.com", "password1");

        assertTrue(utente.isPresent(), "Il login con le credenziali demo deve riuscire");
        assertEquals("Mirco", utente.get().getNome());
        assertEquals("Alessandrini", utente.get().getCognome());
    }

    // Login - Password sbagliata: deve fallire silenziosamente (Optional
    // vuoto), non lanciare un'eccezione. Nessuna scrittura.
    @Test
    void loginConPasswordErrataFallisce() throws Exception {
        final Optional<Utente> utente = autenticazioneService.login("utente1@mail.com", "passwordSbagliata");

        assertTrue(utente.isEmpty(), "Il login con password errata deve restituire Optional vuoto");
    }

    // Login - Email inesistente: stesso comportamento, nessuna eccezione.
    // Nessuna scrittura.
    @Test
    void loginConEmailInesistenteFallisce() throws Exception {
        final Optional<Utente> utente = autenticazioneService.login("nonesiste@example.com", "qualsiasi");

        assertTrue(utente.isEmpty(), "Il login con email inesistente deve restituire Optional vuoto");
    }
}