package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.model.UtenteConRating;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UtenteDAO {

    // U1 - Registrazione utente. Ritorna il codice generato dal DB.
    long insert(Utente utente) throws SQLException;

    // Login (non è un codice ufficiale della specifica, ma necessario all'applicativo):
    // recupera l'utente dalla sua email per verificarne la password.
    Optional<Utente> findByEmail(String email) throws SQLException;

    // U8.2 - Migliori utenti, ordinati per rating medio delle proprie ricette.
    List<UtenteConRating> findMigliori(int limit) throws SQLException;

    // A6 (step 2) - Disattivazione logica di un gruppo di utenti.
    // Riceve la Connection dal chiamante perché fa parte della stessa transazione
    // che poi rimuove anche le loro ricette (RicettaDAO.rimuoviBatch).
    void disattivaBatch(Connection conn, List<Long> codiciUtente) throws SQLException;
}