package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO per U7 - Visualizzazione vantaggi/promozioni attivi. Non corrisponde a
// nessuna singola tabella: unisce SCONTI, PROMOZIONI e CATEGORIE. Query e
// mapping vivono in CatalogoService, non in un DAO (nessun GROUP BY, ma è
// comunque un JOIN a scopo di report/vetrina, non legato al ciclo di vita
// di una singola entità).
public class VantaggioAttivo {

    private final String nomePromo;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final String nomeCategoria;
    private final int minIngredienti;
    private final int maxIngredienti;
    private final BigDecimal percentualeSconto;

    public VantaggioAttivo(final String nomePromo, final LocalDate dataInizio, final LocalDate dataFine,
                            final String nomeCategoria, final int minIngredienti,
                            final int maxIngredienti, final BigDecimal percentualeSconto) {
        this.nomePromo = nomePromo;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.nomeCategoria = nomeCategoria;
        this.minIngredienti = minIngredienti;
        this.maxIngredienti = maxIngredienti;
        this.percentualeSconto = percentualeSconto;
    }

    public String getNomePromo() {
        return nomePromo;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
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