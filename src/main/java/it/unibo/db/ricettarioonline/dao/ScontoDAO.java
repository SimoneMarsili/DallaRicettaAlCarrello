package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Sconto;
import it.unibo.db.ricettarioonline.model.VantaggioAttivo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ScontoDAO {

    // A5 - Pubblicazione sconto. Se la combinazione CodiceCategoria+CodicePromo+MinIngredienti
    // esiste già, aggiorna solo PercentualeSconto (MinIngredienti, essendo parte della PK,
    // non viene mai toccato in fase di update).
    void insertOrUpdate(Sconto sconto) throws SQLException;

    // U9 (step 2a) - Percentuale di sconto migliore attualmente attiva per una ricetta.
    // Riceve la Connection dal chiamante perché deve girare nella stessa transazione
    // dell'ordine in corso. Ritorna sempre un valore (0 se nessuno sconto si applica).
    BigDecimal findMigliorSconto(Connection conn, long codiceRicetta) throws SQLException;

    // U7 - Visualizzazione vantaggi/promozioni attualmente attivi.
    List<VantaggioAttivo> findAttivi() throws SQLException;
}
