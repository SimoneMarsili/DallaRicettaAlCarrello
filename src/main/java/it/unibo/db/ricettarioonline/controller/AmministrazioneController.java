package it.unibo.db.ricettarioonline.controller;

import it.unibo.db.ricettarioonline.model.FatturatoGiornaliero;
import it.unibo.db.ricettarioonline.model.FatturatoRicetta;
import it.unibo.db.ricettarioonline.model.Promozione;
import it.unibo.db.ricettarioonline.model.RecensioneNegativa;
import it.unibo.db.ricettarioonline.service.AmministrazioneService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AmministrazioneController {

    private final AmministrazioneService amministrazioneService = new AmministrazioneService();

    public long inserisciIngrediente(final String nome, final BigDecimal prezzo, final boolean vegano)
            throws SQLException {
        return amministrazioneService.inserisciIngrediente(nome, prezzo, vegano);
    }

    public void aggiornaIngrediente(final long codiceIngrediente, final String nome,
            final BigDecimal prezzo, final Boolean vegano) throws SQLException {
        amministrazioneService.aggiornaIngrediente(codiceIngrediente, nome, prezzo, vegano);
    }

    public long inserisciOAggiornaCategoria(final String nome, final String descrizione) throws SQLException {
        return amministrazioneService.inserisciOAggiornaCategoria(nome, descrizione);
    }

    public long lanciaPromozione(final String nome, final LocalDate dataInizio, final LocalDate dataFine)
            throws SQLException {
        return amministrazioneService.lanciaPromozione(nome, dataInizio, dataFine);
    }

    public List<Promozione> elencaPromozioni() throws SQLException {
        return amministrazioneService.elencaPromozioni();
    }

    public void pubblicaSconto(final long codiceCategoria, final long codicePromo, final int minIngredienti,
            final int maxIngredienti, final BigDecimal percentualeSconto) throws SQLException {
        amministrazioneService.pubblicaSconto(codiceCategoria, codicePromo, minIngredienti,
                maxIngredienti, percentualeSconto);
    }

    public void rimuoviUtentiIncoerenti() throws SQLException {
        amministrazioneService.rimuoviUtentiIncoerenti();
    }

    public List<FatturatoGiornaliero> fatturatoGiornaliero() throws SQLException {
        return amministrazioneService.fatturatoGiornaliero();
    }

    public List<FatturatoRicetta> fatturatoPerRicetta() throws SQLException {
        return amministrazioneService.fatturatoPerRicetta();
    }

    public List<RecensioneNegativa> recensioniNegativeRecenti() throws SQLException {
        return amministrazioneService.recensioniNegativeRecenti();
    }
}
