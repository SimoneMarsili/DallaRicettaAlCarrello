package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO per la vista v_fatturato_giornaliero (solo amministratori).
public class FatturatoGiornaliero {

    private final LocalDate data;
    private final int numeroOrdini;
    private final long quantitaTotaleVenduta;
    private final BigDecimal incassoTotale;

    public FatturatoGiornaliero(final LocalDate data, final int numeroOrdini,
            final long quantitaTotaleVenduta, final BigDecimal incassoTotale) {
        this.data = data;
        this.numeroOrdini = numeroOrdini;
        this.quantitaTotaleVenduta = quantitaTotaleVenduta;
        this.incassoTotale = incassoTotale;
    }

    public LocalDate getData() {
        return data;
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