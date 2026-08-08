package it.unibo.db.ricettarioonline.controller;

import it.unibo.db.ricettarioonline.model.RicettaCompleta;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.service.RicettaService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Pass-through sottile verso RicettaService: nessuna dipendenza da Swing qui.
public class RicettaController {

    private final RicettaService ricettaService = new RicettaService();

    public List<RicettaVista> elencaRicette() throws SQLException {
        return ricettaService.elencaRicette();
    }

    public List<RicettaVista> cercaPerNome(final String nome) throws SQLException {
        return ricettaService.cercaPerNome(nome);
    }

    public List<RicettaVista> cercaPerIngrediente(final String nomeIngrediente) throws SQLException {
        return ricettaService.cercaPerIngrediente(nomeIngrediente);
    }

    public List<RicettaVista> cercaPerTempoMax(final int minuti) throws SQLException {
        return ricettaService.cercaPerTempoMax(minuti);
    }

    public List<RicettaVista> cercaPerPrezzo(final BigDecimal min, final BigDecimal max) throws SQLException {
        return ricettaService.cercaPerPrezzo(min, max);
    }

    public List<RicettaVista> cercaPerAutore(final String nome, final String cognome) throws SQLException {
        return ricettaService.cercaPerAutore(nome, cognome);
    }

    public List<RicettaVista> cercaPerCategoria(final String nomeCategoria) throws SQLException {
        return ricettaService.cercaPerCategoria(nomeCategoria);
    }

    public Optional<RicettaCompleta> getDettaglioRicetta(final long codiceRicetta) throws SQLException {
        return ricettaService.getDettaglioRicetta(codiceRicetta);
    }

    public void pubblicaRecensione(final long codiceUtente, final long codiceRicetta,
            final int voto, final String commento) throws SQLException {
        ricettaService.pubblicaRecensione(codiceUtente, codiceRicetta, voto, commento);
    }

    public long pubblicaRicetta(final String nome, final long codiceUtente, final String preparazione,
        final int tempoRichiesto, final Map<Long, BigDecimal> ingredienti,
        final List<Long> codiciCategoria) throws SQLException {
        return ricettaService.pubblicaRicetta(nome, codiceUtente, preparazione, tempoRichiesto,ingredienti, codiciCategoria);
    }
}