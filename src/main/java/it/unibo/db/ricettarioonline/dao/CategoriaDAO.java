package it.unibo.db.ricettarioonline.dao;

import java.sql.SQLException;
import java.util.List;

import it.unibo.db.ricettarioonline.model.Categoria;

public interface CategoriaDAO {

    // A3 - Inserimento categoria, o aggiornamento della descrizione se il Nome esiste già.
    // Ritorna il codice della categoria (nuova o esistente).
    long insertOrUpdate(Categoria categoria) throws SQLException;

    // Supporto per popolare le scelte disponibili in U6 (classificazione ricetta)
    // e per validare/suggerire il filtro di U3.6.
    List<Categoria> findAll() throws SQLException;
}