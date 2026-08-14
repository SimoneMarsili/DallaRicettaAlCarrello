package it.unibo.db.ricettarioonline.controller;

import it.unibo.db.ricettarioonline.exception.AccountDisattivatoException;
import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.service.AutenticazioneService;

import java.sql.SQLException;
import java.util.Optional;

// Pass-through sottile verso AutenticazioneService: la view non conosce il
// service, solo questo controller. Nessuna dipendenza da Swing qui dentro.
public class LoginController {

    private final AutenticazioneService autenticazioneService = new AutenticazioneService();

    public Optional<Utente> login(final String email, final String passwordChiara)
            throws SQLException, AccountDisattivatoException {
        return autenticazioneService.login(email, passwordChiara);
    }

    public long registraUtente(final String nome, final String cognome, final String email,
            final String passwordChiara, final String indirizzoSpedizione) throws SQLException {
        return autenticazioneService.registraUtente(nome, cognome, email, passwordChiara, indirizzoSpedizione);
    }
}