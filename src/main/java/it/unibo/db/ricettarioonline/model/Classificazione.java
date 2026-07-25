package it.unibo.db.ricettarioonline.model;

public class Classificazione {

    private final long codiceRicetta;
    private final long codiceCategoria;

    public Classificazione(final long codiceRicetta, final long codiceCategoria) {
        this.codiceRicetta = codiceRicetta;
        this.codiceCategoria = codiceCategoria;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public long getCodiceCategoria() {
        return codiceCategoria;
    }
}
