package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

public class DettaglioOrdine {

    private final long codiceOrdine;
    private final long codiceRicetta;
    private final BigDecimal prezzoUnitario;
    private final int quantita;
    private final BigDecimal scontoApplicato;

    public DettaglioOrdine(final long codiceOrdine, final long codiceRicetta,
                            final BigDecimal prezzoUnitario, final int quantita,
                            final BigDecimal scontoApplicato) {
        this.codiceOrdine = codiceOrdine;
        this.codiceRicetta = codiceRicetta;
        this.prezzoUnitario = prezzoUnitario;
        this.quantita = quantita;
        this.scontoApplicato = scontoApplicato;
    }

    public long getCodiceOrdine() {
        return codiceOrdine;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public int getQuantita() {
        return quantita;
    }

    public BigDecimal getScontoApplicato() {
        return scontoApplicato;
    }
}