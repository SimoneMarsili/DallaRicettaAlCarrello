package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

public class DettaglioRicetta {

    private final long codiceRicetta;
    private final long codiceIngrediente;
    private final BigDecimal quantita; // grammi

    public DettaglioRicetta(final long codiceRicetta, final long codiceIngrediente,
                             final BigDecimal quantita) {
        this.codiceRicetta = codiceRicetta;
        this.codiceIngrediente = codiceIngrediente;
        this.quantita = quantita;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public long getCodiceIngrediente() {
        return codiceIngrediente;
    }

    public BigDecimal getQuantita() {
        return quantita;
    }
}