package it.unibo.db.ricettarioonline.model;

public class Categoria {

    private final Long codiceCategoria; // null finché non è stata salvata nel DB
    private final String nome;
    private final String descrizione;

    // Costruttore per una categoria NUOVA (o da inserire/aggiornare via A3):
    // il codice lo assegna il DB.
    public Categoria(final String nome, final String descrizione) {
        this(null, nome, descrizione);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente nel DB.
    public Categoria(final Long codiceCategoria, final String nome, final String descrizione) {
        this.codiceCategoria = codiceCategoria;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    public Long getCodiceCategoria() {
        return codiceCategoria;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public String toString() {
        return nome;
    }
}
