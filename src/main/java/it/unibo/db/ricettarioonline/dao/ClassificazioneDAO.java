package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.Classificazione;

import java.sql.SQLException;
import java.util.List;

public interface ClassificazioneDAO {

    // U6 (loop k volte) - Associazione ricetta-categoria.
    void insert(Classificazione classificazione) throws SQLException;

    // U11.3 - Categorie di una ricetta (terza delle 3 query separate).
    // CLASSIFICAZIONI non ha attributi propri oltre alle due FK, quindi il
    // risultato è semplicemente l'elenco delle Categoria associate: nessun
    // tipo dedicato necessario.
    List<Categoria> findByRicetta(long codiceRicetta) throws SQLException;
}
