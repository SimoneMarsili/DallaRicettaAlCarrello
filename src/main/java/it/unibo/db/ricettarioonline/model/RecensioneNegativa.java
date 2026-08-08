package it.unibo.db.ricettarioonline.model;

import java.time.LocalDate;

// DTO per la vista v_recensioni_negative_recenti (solo amministratori).
public class RecensioneNegativa {

    private final long codiceRicetta;
    private final String nomeRicetta;
    private final long codiceUtente;
    private final String nomeUtente;
    private final String cognomeUtente;
    private final int voto;
    private final String commento;
    private final LocalDate data;

    public RecensioneNegativa(final long codiceRicetta, final String nomeRicetta, final long codiceUtente,
            final String nomeUtente, final String cognomeUtente, final int voto,
            final String commento, final LocalDate data) {
        this.codiceRicetta = codiceRicetta;
        this.nomeRicetta = nomeRicetta;
        this.codiceUtente = codiceUtente;
        this.nomeUtente = nomeUtente;
        this.cognomeUtente = cognomeUtente;
        this.voto = voto;
        this.commento = commento;
        this.data = data;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public String getNomeRicetta() {
        return nomeRicetta;
    }

    public long getCodiceUtente() {
        return codiceUtente;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public int getVoto() {
        return voto;
    }

    public String getCommento() {
        return commento;
    }

    public LocalDate getData() {
        return data;
    }
}