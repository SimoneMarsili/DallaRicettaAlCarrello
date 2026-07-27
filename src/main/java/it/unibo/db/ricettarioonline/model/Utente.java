package it.unibo.db.ricettarioonline.model;

public class Utente {

    private final long codiceUtente;
    private final String nome;
    private final String cognome;
    private final String email;
    private final String password; // hash SHA2, letto dal DB - mai testo in chiaro
    private final String ruolo;
    private final boolean attivo;
    private final String indirizzoSpedizione; // può essere null (es. ADMIN)

    public Utente(final long codiceUtente, final String nome, final String cognome,
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

    public long getCodiceUtente() {
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