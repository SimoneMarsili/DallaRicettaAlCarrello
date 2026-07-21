package it.unibo.db.ricettarioonline.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import it.unibo.db.ricettarioonline.model.Ingrediente;

public interface IngredienteDAO {

    // A1 - Inserimento ingrediente. Ritorna il codiceIngrediente generato dal DB.
    long insert(Ingrediente ingrediente) throws SQLException;

    // A2 - Aggiornamento parziale: i parametri null vengono ignorati (COALESCE lato SQL).
    void update(long codiceIngrediente, String nome, BigDecimal prezzo, Boolean vegano) throws SQLException;

    // U4 - Visualizzazione ingredienti.
    List<Ingrediente> findAll() throws SQLException;
}
