package it.unibo.db.ricettarioonline.model;

import java.util.List;

// Aggregato di visualizzazione per U11: assembla le tre query separate
// (dati ricetta, ingredienti, categorie) in un solo oggetto da restituire
// al chiamante. Le tre query restano separate nei rispettivi DAO;
// l'assemblaggio avviene qui, nel service.
public class RicettaCompleta {

    private final RicettaVista ricetta;
    private final List<IngredienteRicetta> ingredienti;
    private final List<Categoria> categorie;

    public RicettaCompleta(final RicettaVista ricetta, final List<IngredienteRicetta> ingredienti,
                            final List<Categoria> categorie) {
        this.ricetta = ricetta;
        this.ingredienti = ingredienti;
        this.categorie = categorie;
    }

    public RicettaVista getRicetta() {
        return ricetta;
    }

    public List<IngredienteRicetta> getIngredienti() {
        return ingredienti;
    }

    public List<Categoria> getCategorie() {
        return categorie;
    }
}