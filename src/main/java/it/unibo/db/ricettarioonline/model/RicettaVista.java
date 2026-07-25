package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

// Proiezione di visualizzazione per le ricette: usata sia dalle liste (U2, U3.1-U3.6)
// sia dal dettaglio (U11.1). Non è un report: nessuna aggregazione, solo dati
// arricchiti col nome dell'autore tramite JOIN a UTENTI.
public class RicettaVista {

    private final long codiceRicetta;
    private final String nomeRicetta;
    private final String preparazione;
    private final int tempoRichiesto;
    private final BigDecimal prezzoRicetta;
    private final BigDecimal mediaRecensioni;
    private final String nomeAutore;
    private final String cognomeAutore;

    public RicettaVista(final long codiceRicetta, final String nomeRicetta,
                         final String preparazione, final int tempoRichiesto,
                         final BigDecimal prezzoRicetta, final BigDecimal mediaRecensioni,
                         final String nomeAutore, final String cognomeAutore) {
        this.codiceRicetta = codiceRicetta;
        this.nomeRicetta = nomeRicetta;
        this.preparazione = preparazione;
        this.tempoRichiesto = tempoRichiesto;
        this.prezzoRicetta = prezzoRicetta;
        this.mediaRecensioni = mediaRecensioni;
        this.nomeAutore = nomeAutore;
        this.cognomeAutore = cognomeAutore;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public String getNomeRicetta() {
        return nomeRicetta;
    }

    public String getPreparazione() {
        return preparazione;
    }

    public int getTempoRichiesto() {
        return tempoRichiesto;
    }

    public BigDecimal getPrezzoRicetta() {
        return prezzoRicetta;
    }

    public BigDecimal getMediaRecensioni() {
        return mediaRecensioni;
    }

    public String getNomeAutore() {
        return nomeAutore;
    }

    public String getCognomeAutore() {
        return cognomeAutore;
    }
}
