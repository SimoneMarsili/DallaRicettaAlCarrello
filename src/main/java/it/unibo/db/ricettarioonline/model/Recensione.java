package it.unibo.db.ricettarioonline.model;

import java.time.LocalDate;

public class Recensione {

    private final long codiceUtente;
    private final long codiceRicetta;
    private final LocalDate data;
    private final int voto;
    private final String commento; // può essere null (colonna nullable)

    // Costruttore per una recensione NUOVA (U5): la Data viene impostata dal DB
    // stesso (CURRENT_DATE nella query), quindi non la chiediamo qui.
    public Recensione(final long codiceUtente, final long codiceRicetta,
                       final int voto, final String commento) {
        this(codiceUtente, codiceRicetta, null, voto, commento);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente dal DB.
    public Recensione(final long codiceUtente, final long codiceRicetta, final LocalDate data,
                       final int voto, final String commento) {
        this.codiceUtente = codiceUtente;
        this.codiceRicetta = codiceRicetta;
        this.data = data;
        this.voto = voto;
        this.commento = commento;
    }

    public long getCodiceUtente() {
        return codiceUtente;
    }

    public long getCodiceRicetta() {
        return codiceRicetta;
    }

    public LocalDate getData() {
        return data;
    }

    public int getVoto() {
        return voto;
    }

    public String getCommento() {
        return commento;
    }
}