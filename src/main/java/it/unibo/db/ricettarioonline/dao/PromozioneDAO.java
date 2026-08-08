package it.unibo.db.ricettarioonline.dao;

import java.sql.SQLException;
import java.util.List;

import it.unibo.db.ricettarioonline.model.Promozione;

public interface PromozioneDAO {

    // A4 - Lancio promozione. Ritorna il codice generato dal DB.
    long insert(Promozione promozione) throws SQLException;

    List<Promozione> findAll() throws SQLException;
}
