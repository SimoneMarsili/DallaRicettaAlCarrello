package it.unibo.db.ricettarioonline.model;

import java.time.LocalDate;

public class Promozione {

    private final Long codicePromo; // null finché non è stata salvata nel DB
    private final String nome;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;

    // Costruttore per una promozione NUOVA (A4): il codice lo assegna il DB.
    public Promozione(final String nome, final LocalDate dataInizio, final LocalDate dataFine) {
        this(null, nome, dataInizio, dataFine);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente nel DB.
    public Promozione(final Long codicePromo, final String nome,
                       final LocalDate dataInizio, final LocalDate dataFine) {
        this.codicePromo = codicePromo;
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public Long getCodicePromo() {
        return codicePromo;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }
}
