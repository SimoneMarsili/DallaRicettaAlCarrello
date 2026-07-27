package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.DettaglioOrdine;

import java.sql.SQLException;

public interface DettaglioOrdineDAO {

    // U9 (step 2b, loop j volte) - Riga di dettaglio per una ricetta nel carrello.
    // La Connection arriva solo dal costruttore: per l'uso dentro la
    // transazione di U9, il service creerà un'istanza apposita.
    void insert(DettaglioOrdine dettaglioOrdine) throws SQLException;
}