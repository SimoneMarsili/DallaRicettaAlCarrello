package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

public class Ricetta {

    private final Long codiceRicetta; // null finché non è stata salvata nel DB
    private final String nome;
    private final long codiceUtente;
    private final String preparazione;
    private final int tempoRichiesto;
    private final int numeroIngredienti;   // derivato, mantenuto dai trigger
    private final BigDecimal prezzoRicetta; // derivato, mantenuto dai trigger
    private final BigDecimal mediaRecensioni; // derivato, mantenuto dai trigger
    private final boolean rimossa;

    // Costruttore per una ricetta NUOVA (U6): NumeroIngredienti, PrezzoRicetta
    // e MediaRecensioni partono da 0 e li aggiornano i trigger via via che
    // vengono inserite le righe di DETTAGLI_RICETTA/RECENSIONI. Rimossa parte FALSE.
    public Ricetta(final String nome, final long codiceUtente,
                    final String preparazione, final int tempoRichiesto) {
        this(null, nome, codiceUtente, preparazione, tempoRichiesto,
                0, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente dal DB.
    public Ricetta(final Long codiceRicetta, final String nome, final long codiceUtente,
                    final String preparazione, final int tempoRichiesto,
                    final int numeroIngredienti, final BigDecimal prezzoRicetta,
                    final BigDecimal mediaRecensioni, final boolean rimossa) {
        this.codiceRicetta = codiceRicetta;
        this.nome = nome;
        this.codiceUtente = codiceUtente;
        this.preparazione = preparazione;
        this.tempoRichiesto = tempoRichiesto;
        this.numeroIngredienti = numeroIngredienti;
        this.prezzoRicetta = prezzoRicetta;
        this.mediaRecensioni = mediaRecensioni;
        this.rimossa = rimossa;
    }

    public Long getCodiceRicetta() {
        return codiceRicetta;
    }

    public String getNome() {
        return nome;
    }

    public long getCodiceUtente() {
        return codiceUtente;
    }

    public String getPreparazione() {
        return preparazione;
    }

    public int getTempoRichiesto() {
        return tempoRichiesto;
    }

    public int getNumeroIngredienti() {
        return numeroIngredienti;
    }

    public BigDecimal getPrezzoRicetta() {
        return prezzoRicetta;
    }

    public BigDecimal getMediaRecensioni() {
        return mediaRecensioni;
    }

    public boolean isRimossa() {
        return rimossa;
    }
}