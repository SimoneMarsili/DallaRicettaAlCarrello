package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

// DTO per U8.2 - Migliori utenti (classifica per rating medio delle proprie ricette).
public class UtenteConRating {

    private final long codiceUtente;
    private final String nome;
    private final String cognome;
    private final BigDecimal ratingUtente;
    private final int numeroRicette;

    public UtenteConRating(final long codiceUtente, final String nome, final String cognome,
                            final BigDecimal ratingUtente, final int numeroRicette) {
        this.codiceUtente = codiceUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.ratingUtente = ratingUtente;
        this.numeroRicette = numeroRicette;
    }

    public long getCodiceUtente() {
        return codiceUtente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public BigDecimal getRatingUtente() {
        return ratingUtente;
    }

    public int getNumeroRicette() {
        return numeroRicette;
    }
}