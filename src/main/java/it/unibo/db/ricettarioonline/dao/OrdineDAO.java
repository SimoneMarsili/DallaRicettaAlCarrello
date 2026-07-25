package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Ordine;
import it.unibo.db.ricettarioonline.model.RigaStoricoOrdine;

import java.sql.SQLException;
import java.util.List;

public interface OrdineDAO {

    // U9 (step 1) - Creazione ordine. Ritorna il codice generato dal DB.
    // La Connection arriva solo dal costruttore: per l'uso dentro la
    // transazione di U9, il service creerà un'istanza apposita.
    long insert(Ordine ordine) throws SQLException;

    // U10 - Storico ordini di un utente, con dettaglio riga per riga.
    // Resta qui (non in DettaglioOrdineDAO) perché lo scope della domanda
    // è "tutti gli ordini di un utente", un concetto legato a Ordine,
    // non un drill-down su un singolo ordine specifico.
    List<RigaStoricoOrdine> findStoricoByUtente(long codiceUtente) throws SQLException;
}
