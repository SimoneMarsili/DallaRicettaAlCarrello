package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.DettaglioRicetta;
import it.unibo.db.ricettarioonline.model.IngredienteRicetta;

import java.sql.SQLException;
import java.util.List;

public interface DettaglioRicettaDAO {

    // U6 (loop n volte) - Associazione ricetta-ingrediente con quantità.
    // il service, quando
    // deve inserire questi dati dentro la transazione di U6, creerà
    // un'istanza di JdbcDettaglioRicettaDAO con la Connection della transazione.
    void insert(DettaglioRicetta dettaglioRicetta) throws SQLException;

    // U11.2 - Ingredienti di una ricetta (seconda delle 3 query separate).
    List<IngredienteRicetta> findByRicetta(long codiceRicetta) throws SQLException;
}