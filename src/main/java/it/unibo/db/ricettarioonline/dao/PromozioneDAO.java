package it.unibo.db.ricettarioonline.dao;

import java.sql.SQLException;

import it.unibo.db.ricettarioonline.model.Promozione;

public interface PromozioneDAO {

    // A4 - Lancio promozione. Ritorna il codice generato dal DB.
    long insert(Promozione promozione) throws SQLException;
}
