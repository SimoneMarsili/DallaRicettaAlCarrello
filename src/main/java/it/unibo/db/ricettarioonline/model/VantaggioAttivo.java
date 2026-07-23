package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO per U7 - Visualizzazione vantaggi/promozioni.
// Non corrisponde a nessuna singola tabella: unisce SCONTI, PROMOZIONI e CATEGORIE.
public record VantaggioAttivo(
        String nomePromo,
        LocalDate dataInizio,
        LocalDate dataFine,
        String nomeCategoria,
        int minIngredienti,
        int maxIngredienti,
        BigDecimal percentualeSconto) {
}