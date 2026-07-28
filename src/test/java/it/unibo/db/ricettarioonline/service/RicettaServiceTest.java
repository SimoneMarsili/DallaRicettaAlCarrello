package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.RicettaCompleta;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RicettaServiceTest {

    private final RicettaService ricettaService = new RicettaService();
    private final AmministrazioneService amministrazioneService = new AmministrazioneService();
    private final AutenticazioneService autenticazioneService = new AutenticazioneService();
    private final CatalogoService catalogoService = new CatalogoService();

    // Ordine di pulizia: prima le ricette (CASCADE su DETTAGLI_RICETTA,
    // CLASSIFICAZIONI, RECENSIONI), poi gli utenti (ora liberi da RESTRICT),
    // infine eventuali categorie di test.
    private final List<Long> ricetteDaRipulire = new ArrayList<>();
    private final List<Long> utentiDaRipulire = new ArrayList<>();
    private final List<Long> categorieDaRipulire = new ArrayList<>();

    @AfterEach
    void pulisci() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            eliminaTutti(connection, "DELETE FROM RICETTE WHERE CodiceRicetta = ?", ricetteDaRipulire);
            eliminaTutti(connection, "DELETE FROM UTENTI WHERE CodiceUtente = ?", utentiDaRipulire);
            eliminaTutti(connection, "DELETE FROM CATEGORIE WHERE CodiceCategoria = ?", categorieDaRipulire);
        }
        ricetteDaRipulire.clear();
        utentiDaRipulire.clear();
        categorieDaRipulire.clear();
    }

    private void eliminaTutti(final Connection connection, final String sql, final List<Long> codici)
            throws Exception {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (final Long codice : codici) {
                ps.setLong(1, codice);
                ps.executeUpdate();
            }
        }
    }

    // U2 - Lo script di popolamento crea 6 ricette: la lista non deve essere vuota.
    @Test
    void elencoRicetteNonVuoto() throws Exception {
        final List<RicettaVista> ricette = ricettaService.elencaRicette();

        assertFalse(ricette.isEmpty(), "L'elenco ricette non deve essere vuoto su un DB popolato");
    }

    // U3.1 - Test con dati noti: la Carbonara ha TempoRichiesto=30 e autore
    // Mirco Alessandrini secondo lo script di popolamento.
    @Test
    void cercaPerNomeTrovaCarbonaraConDatiCorretti() throws Exception {
        final List<RicettaVista> risultati = ricettaService.cercaPerNome("Carbonara");

        assertEquals(1, risultati.size(), "Deve esserci esattamente una ricetta chiamata Carbonara");
        assertEquals(30, risultati.get(0).getTempoRichiesto());
        assertEquals("Mirco", risultati.get(0).getNomeAutore());
        assertEquals("Alessandrini", risultati.get(0).getCognomeAutore());
    }

    // U3.2 - Spaghetti compare in 4 ricette demo: Carbonara, Amatriciana,
    // Spaghetti al pomodoro, Cacio e pepe.
    @Test
    void cercaPerIngredienteTrovaRicetteConSpaghetti() throws Exception {
        final List<RicettaVista> risultati = ricettaService.cercaPerIngrediente("Spaghetti");

        final List<String> nomi = risultati.stream().map(RicettaVista::getNomeRicetta).toList();
        assertTrue(nomi.contains("Carbonara"));
        assertTrue(nomi.contains("Amatriciana"));
        assertTrue(nomi.contains("Spaghetti al pomodoro"));
        assertTrue(nomi.contains("Cacio e pepe"));
    }

    // U3.6 - 5 delle 6 ricette demo sono classificate "Italiano" (tutte tranne
    // Purè di patate, che è solo "Vegetariano").
    @Test
    void cercaPerCategoriaTrovaRicetteItaliane() throws Exception {
        final List<RicettaVista> risultati = ricettaService.cercaPerCategoria("Italiano");

        final List<String> nomi = risultati.stream().map(RicettaVista::getNomeRicetta).toList();
        assertTrue(nomi.contains("Amatriciana"));
        assertFalse(nomi.contains("Purè di patate"), "Purè di patate non è classificata come Italiano");
    }

    // U5 - Pubblica una recensione su una ricetta creata appositamente per
    // questo test (per non alterare le medie delle ricette demo, usate anche
    // da ReportServiceTest). Verifica che il trigger AGGIORNI MediaRecensioni.
    @Test
    void pubblicaRecensioneAggiornaMediaRecensioni() throws Exception {
        final long codiceUtenteAutore = registraUtenteDiTest();
        final long codiceUtenteRecensore = registraUtenteDiTest();

        final long codiceRicetta = ricettaService.pubblicaRicetta(
                "RicettaPerRecensione" + System.currentTimeMillis(), codiceUtenteAutore,
                "Preparazione di prova", 10, Map.of(), List.of());
        ricetteDaRipulire.add(codiceRicetta);

        ricettaService.pubblicaRecensione(codiceUtenteRecensore, codiceRicetta, 8, "Voto di prova");

        final Optional<RicettaCompleta> dettaglio = ricettaService.getDettaglioRicetta(codiceRicetta);
        assertTrue(dettaglio.isPresent());
        assertEquals(0, new BigDecimal("8.00").compareTo(dettaglio.get().getRicetta().getMediaRecensioni()),
                "Con una sola recensione di voto 8, la media deve essere 8.00");
    }

    // U6 + U11 - Pubblica una ricetta con un ingrediente demo e una categoria
    // di test, poi verifica tramite U11 che tutto sia stato salvato
    // correttamente e che i trigger (NumeroIngredienti, PrezzoRicetta) abbiano
    // fatto il loro lavoro.
    @Test
    void pubblicaRicettaCreaRicettaConIngredientiECategorie() throws Exception {
        final long codiceUtente = registraUtenteDiTest();

        final long codiceCategoria = amministrazioneService.inserisciOAggiornaCategoria(
                "CategoriaRicettaTest" + System.currentTimeMillis(), "Per test U6");
        categorieDaRipulire.add(codiceCategoria);

        final Ingrediente farina = catalogoService.elencaIngredienti().stream()
                .filter(i -> "Farina 00".equals(i.getNome()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingrediente demo 'Farina 00' non trovato"));

        final long codiceRicetta = ricettaService.pubblicaRicetta(
                "RicettaDiTest" + System.currentTimeMillis(), codiceUtente,
                "Preparazione di prova", 15,
                Map.of(farina.getCodiceIngrediente(), new BigDecimal("100")),
                List.of(codiceCategoria));
        ricetteDaRipulire.add(codiceRicetta);

        assertTrue(codiceRicetta > 0);

        final Optional<RicettaCompleta> dettaglio = ricettaService.getDettaglioRicetta(codiceRicetta);
        assertTrue(dettaglio.isPresent(), "La ricetta appena creata deve essere trovabile via U11");

        // PrezzoRicetta atteso dal trigger: 100g / 1000 * prezzo(Farina 00).
        final BigDecimal prezzoAtteso = new BigDecimal("100")
                .divide(new BigDecimal("1000"))
                .multiply(farina.getPrezzo());
        assertEquals(0, prezzoAtteso.compareTo(dettaglio.get().getRicetta().getPrezzoRicetta()),
                "PrezzoRicetta deve essere calcolato dal trigger in base alla quantità di Farina 00");

        assertEquals(1, dettaglio.get().getIngredienti().size());
        assertEquals("Farina 00", dettaglio.get().getIngredienti().get(0).getNome());

        assertEquals(1, dettaglio.get().getCategorie().size());
        assertEquals(codiceCategoria, dettaglio.get().getCategorie().get(0).getCodiceCategoria());
    }

    // Helper: registra un utente di test tramite AutenticazioneService,
    // aggiungendolo alla lista di pulizia. Riusato da più test in questa classe.
    private long registraUtenteDiTest() throws Exception {
        final String email = "test.ricetta." + System.nanoTime() + "@example.com";
        final long codiceUtente = autenticazioneService.registraUtente(
                "Test", "Utente", email, "password", "Via di Prova 1, Bologna");
        utentiDaRipulire.add(codiceUtente);
        return codiceUtente;
    }
}