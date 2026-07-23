package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

public class Sconto {

    private final long codiceCategoria;
    private final long codicePromo;
    private final int minIngredienti;  // parte della PK, non aggiornabile
    private final int maxIngredienti;
    private final BigDecimal percentualeSconto;

    public Sconto(final long codiceCategoria, final long codicePromo,
                   final int minIngredienti, final int maxIngredienti,
                   final BigDecimal percentualeSconto) {
        this.codiceCategoria = codiceCategoria;
        this.codicePromo = codicePromo;
        this.minIngredienti = minIngredienti;
        this.maxIngredienti = maxIngredienti;
        this.percentualeSconto = percentualeSconto;
    }
    
    public long getCodiceCategoria() {
        return codiceCategoria;
    }

    public long getCodicePromo() {
        return codicePromo;
    }

    public int getMinIngredienti() {
        return minIngredienti;
    }

    public int getMaxIngredienti() {
        return maxIngredienti;
    }

    public BigDecimal getPercentualeSconto() {
        return percentualeSconto;
    }
}