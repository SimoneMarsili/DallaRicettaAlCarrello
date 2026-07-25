package it.unibo.db.ricettarioonline.model;

import java.time.LocalDate;

public class Ordine {

    private final Long codiceOrdine; // null finché non è stato salvato nel DB
    private final LocalDate data;
    private final String note; // può essere null (colonna nullable)
    private final long codiceUtente;

    // Costruttore per un ordine NUOVO (U9): la Data viene impostata dal DB
    // stesso (CURRENT_DATE nella query), quindi non la chiediamo qui.
    public Ordine(final String note, final long codiceUtente) {
        this(null, null, note, codiceUtente);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente dal DB.
    public Ordine(final Long codiceOrdine, final LocalDate data,
                   final String note, final long codiceUtente) {
        this.codiceOrdine = codiceOrdine;
        this.data = data;
        this.note = note;
        this.codiceUtente = codiceUtente;
    }

    public Long getCodiceOrdine() {
        return codiceOrdine;
    }

    public LocalDate getData() {
        return data;
    }

    public String getNote() {
        return note;
    }

    public long getCodiceUtente() {
        return codiceUtente;
    }
}