package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

// DTO per U8.3 - Ricette più ordinate (classifica per quantità totale ordinata).
public class RicettaOrdinata {

    private final long codiceRicetta;
    private final String nomeRicetta;
    private final BigDecimal prezzoRicetta;
    private final long quantitaTotaleOrdinata;

    public RicettaOrdinata(final long codiceRicetta, final String nomeRicetta,
                            final BigDecimal prezzoRicetta, final long quantitaTotaleOrdinata) {
        this.codiceRicetta = codiceRicetta;
        this.nomeRicetta = nomeRicetta;
        this.prezzoRicetta = prezzoRicetta;
        this.quantitaTotaleOrdinata = quantitaTotaleOrdinata;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public String getNomeRicetta() {
        return nomeRicetta;
    }

    public BigDecimal getPrezzoRicetta() {
        return prezzoRicetta;
    }

    public long getQuantitaTotaleOrdinata() {
        return quantitaTotaleOrdinata;
    }
}