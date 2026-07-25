package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Recensione;

import java.sql.SQLException;

public interface RecensioneDAO {

    // U5 - Pubblicazione recensione.
    void insert(Recensione recensione) throws SQLException;
}
