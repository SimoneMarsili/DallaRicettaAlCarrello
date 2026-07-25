package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// Proiezione di visualizzazione per U10: una riga dello storico ordini di un
// utente, con i dati della ricetta e il totale già calcolato. Non è un report:
// nessuna aggregazione, una riga per ogni riga reale di DETTAGLI_ORDINE.
public class RigaStoricoOrdine {

    private final long codiceOrdine;
    private final LocalDate data;
    private final long codiceRicetta;
    private final String nomeRicetta;
    private final int quantita;
    private final BigDecimal prezzoUnitario;
    private final BigDecimal scontoApplicato;
    private final BigDecimal totaleRiga;

    public RigaStoricoOrdine(final long codiceOrdine, final LocalDate data,
                              final long codiceRicetta, final String nomeRicetta,
                              final int quantita, final BigDecimal prezzoUnitario,
                              final BigDecimal scontoApplicato, final BigDecimal totaleRiga) {
        this.codiceOrdine = codiceOrdine;
        this.data = data;
        this.codiceRicetta = codiceRicetta;
        this.nomeRicetta = nomeRicetta;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.scontoApplicato = scontoApplicato;
        this.totaleRiga = totaleRiga;
    }

    public long getCodiceOrdine() {
        return codiceOrdine;
    }

    public LocalDate getData() {
        return data;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public String getNomeRicetta() {
        return nomeRicetta;
    }

    public int getQuantita() {
        return quantita;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public BigDecimal getScontoApplicato() {
        return scontoApplicato;
    }

    public BigDecimal getTotaleRiga() {
        return totaleRiga;
    }
}