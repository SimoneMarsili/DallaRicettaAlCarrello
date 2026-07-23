package it.unibo.db.ricettarioonline.model;

public class Utente {

    private final Long codiceUtente; // null finché non è stato salvato nel DB
    private final String nome;
    private final String cognome;
    private final String email;
    private final String password; // hash, mai in chiaro
    private final String ruolo;
    private final boolean attivo;
    private final String indirizzoSpedizione; // può essere null (es. ADMIN)

    // Costruttore per un utente NUOVO (U1): Ruolo e Attivo sono fissati dalla
    // query stessa ('UTENTE', TRUE), quindi non li chiediamo qui.
    public Utente(final String nome, final String cognome, final String email,
                  final String password, final String indirizzoSpedizione) {
        this(null, nome, cognome, email, password, "UTENTE", true, indirizzoSpedizione);
    }

    // Costruttore completo, usato quando leggiamo una riga già esistente nel DB.
    public Utente(final Long codiceUtente, final String nome, final String cognome,
                  final String email, final String password, final String ruolo,
                  final boolean attivo, final String indirizzoSpedizione) {
        this.codiceUtente = codiceUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.attivo = attivo;
        this.indirizzoSpedizione = indirizzoSpedizione;
    }

    public Long getCodiceUtente() {
        return codiceUtente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public String getIndirizzoSpedizione() {
        return indirizzoSpedizione;
    }
}