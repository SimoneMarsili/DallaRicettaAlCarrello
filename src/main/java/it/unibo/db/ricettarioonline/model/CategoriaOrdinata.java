package it.unibo.db.ricettarioonline.model;

// DTO per U8.4 - Categorie più ordinate (classifica per quantità totale ordinata).
public class CategoriaOrdinata {

    private final long codiceCategoria;
    private final String nomeCategoria;
    private final long quantitaTotaleOrdinata;

    public CategoriaOrdinata(final long codiceCategoria, final String nomeCategoria,
                              final long quantitaTotaleOrdinata) {
        this.codiceCategoria = codiceCategoria;
        this.nomeCategoria = nomeCategoria;
        this.quantitaTotaleOrdinata = quantitaTotaleOrdinata;
    }

    public long getCodiceCategoria() {
        return codiceCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public long getQuantitaTotaleOrdinata() {
        return quantitaTotaleOrdinata;
    }
}
