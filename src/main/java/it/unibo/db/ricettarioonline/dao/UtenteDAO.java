package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Utente;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UtenteDAO {

    // U1 - Registrazione utente. La password arriva in chiaro: l'hashing
    // (SHA2) viene fatto lato SQL, mai calcolato in Java. Ritorna il codice
    // generato dal DB.
    long registra(String nome, String cognome, String email, String passwordChiara,
            String indirizzoSpedizione) throws SQLException;

    // Login: ricerca per email e verifica password in un solo passaggio
    // (confronto SHA2 fatto direttamente dal DB, non in Java).
    Optional<Utente> login(String email, String passwordChiara) throws SQLException;

    // A6 (step 2) - Disattivazione logica di un gruppo di utenti.
    void disattivaBatch(List<Long> codiciUtente) throws SQLException;
}