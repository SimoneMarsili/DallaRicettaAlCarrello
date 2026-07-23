package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

// DTO per U8.2 - Migliori utenti. Non è un utente puro: aggiunge il rating medio
// e il numero di ricette, calcolati con AVG/COUNT sulle sue ricette.
public record UtenteConRating(
        long codiceUtente,
        String nome,
        String cognome,
        BigDecimal ratingUtente,
        int numeroRicette) {
}