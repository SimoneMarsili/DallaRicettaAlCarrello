package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

// DTO per la vista v_fatturato_per_ricetta (solo amministratori).
public class FatturatoRicetta {

    private final long codiceRicetta;
    private final String nomeRicetta;
    private final int numeroOrdini;
    private final long quantitaTotaleVenduta;
    private final BigDecimal incassoTotale;

    public FatturatoRicetta(final long codiceRicetta, final String nomeRicetta, final int numeroOrdini,
            final long quantitaTotaleVenduta, final BigDecimal incassoTotale) {
        this.codiceRicetta = codiceRicetta;
        this.nomeRicetta = nomeRicetta;
        this.numeroOrdini = numeroOrdini;
        this.quantitaTotaleVenduta = quantitaTotaleVenduta;
        this.incassoTotale = incassoTotale;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public String getNomeRicetta() {
        return nomeRicetta;
    }

    public int getNumeroOrdini() {
        return numeroOrdini;
    }

    public long getQuantitaTotaleVenduta() {
        return quantitaTotaleVenduta;
    }

    public BigDecimal getIncassoTotale() {
        return incassoTotale;
    }
}