package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

public class Ingrediente {

    private final Long codiceIngrediente; // null finché non è stato salvato nel DB
    private final String nome;
    private final BigDecimal prezzo;      // null quando non viene selezionato dalla query (vedi U4)
    private final boolean vegano;

    // Costruttore per un ingrediente NUOVO, non ancora persistito (A1):
    // non ha ancora un codice, lo assegnerà il DB con l'AUTO_INCREMENT.
    public Ingrediente(final String nome, final BigDecimal prezzo, final boolean vegano) {
        this(null, nome, prezzo, vegano);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente nel DB.
    public Ingrediente(final Long codiceIngrediente, final String nome,
                        final BigDecimal prezzo, final boolean vegano) {
        this.codiceIngrediente = codiceIngrediente;
        this.nome = nome;
        this.prezzo = prezzo;
        this.vegano = vegano;
    }

    public Long getCodiceIngrediente() {
        return codiceIngrediente;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public boolean isVegano() {
        return vegano;
    }
}