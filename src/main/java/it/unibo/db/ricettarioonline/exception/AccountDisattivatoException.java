package it.unibo.db.ricettarioonline.exception;

// Lanciata quando un utente con credenziali corrette prova ad accedere ma
// il suo account è stato disattivato (Attivo = FALSE, tipicamente da A6).
// Eccezione checked, coerente con SQLException usata ovunque nel progetto:
// costringe chi chiama login() a gestire esplicitamente questo caso.
public class AccountDisattivatoException extends Exception {

    public AccountDisattivatoException(final String message) {
        super(message);
    }
}