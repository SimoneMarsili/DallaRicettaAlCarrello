package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmministrazioneServiceTest {

    private final AmministrazioneService amministrazioneService = new AmministrazioneService();
    private final CatalogoService catalogoService = new CatalogoService();
    private final AutenticazioneService autenticazioneService = new AutenticazioneService();
    private final RicettaService ricettaService = new RicettaService();

    // Ordine di pulizia: prima RICETTE (CASCADE su DETTAGLI_RICETTA,
    // CLASSIFICAZIONI, RECENSIONI), poi UTENTI (liberi da RESTRICT a quel
    // punto), poi SCONTI (dipende da CATEGORIE/PROMOZIONI), infine
    // CATEGORIE/PROMOZIONI/INGREDIENTI, indipendenti tra loro.
    private final List<Long> ricetteDaRipulire = new ArrayList<>();
    private final List<Long> utentiDaRipulire = new ArrayList<>();
    private final List<Long> ingredientiDaRipulire = new ArrayList<>();
    private final List<Long> categorieDaRipulire = new ArrayList<>();
    private final List<Long> promozioniDaRipulire = new ArrayList<>();

    @AfterEach
    void pulisci() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            eliminaTutti(connection, "DELETE FROM RICETTE WHERE CodiceRicetta = ?", ricetteDaRipulire);
            eliminaTutti(connection, "DELETE FROM UTENTI WHERE CodiceUtente = ?", utentiDaRipulire);
            eliminaTutti(connection, "DELETE FROM SCONTI WHERE CodiceCategoria = ?", categorieDaRipulire);
            eliminaTutti(connection, "DELETE FROM CATEGORIE WHERE CodiceCategoria = ?", categorieDaRipulire);
            eliminaTutti(connection, "DELETE FROM PROMOZIONI WHERE CodicePromo = ?", promozioniDaRipulire);
            eliminaTutti(connection, "DELETE FROM INGREDIENTI WHERE CodiceIngrediente = ?", ingredientiDaRipulire);
        }
        ricetteDaRipulire.clear();
        utentiDaRipulire.clear();
        categorieDaRipulire.clear();
        promozioniDaRipulire.clear();
        ingredientiDaRipulire.clear();
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

    // A1 - Inserimento ingrediente, poi verifica tramite U4 che sia presente
    // con i valori corretti.
    @Test
    void inserisceIngredienteCorrettamente() throws Exception {
        final String nome = "IngredienteTest" + System.currentTimeMillis();

        final long codice = amministrazioneService.inserisciIngrediente(nome, new BigDecimal("9.99"), true);
        ingredientiDaRipulire.add(codice);

        assertTrue(codice > 0, "Il codice generato deve essere positivo");

        final Ingrediente trovato = catalogoService.elencaIngredienti().stream()
                .filter(i -> i.getCodiceIngrediente().equals(codice))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingrediente appena inserito non trovato"));

        assertEquals(nome, trovato.getNome());
        assertTrue(trovato.isVegano());
        assertEquals(0, new BigDecimal("9.99").compareTo(trovato.getPrezzo()));
    }

    // A2 - Aggiornamento parziale: cambia solo il prezzo, nome e vegano restano invariati.
    @Test
    void aggiornaIngredienteParzialmente() throws Exception {
        final String nome = "IngredienteTest" + System.currentTimeMillis();
        final long codice = amministrazioneService.inserisciIngrediente(nome, new BigDecimal("5.00"), false);
        ingredientiDaRipulire.add(codice);

        amministrazioneService.aggiornaIngrediente(codice, null, new BigDecimal("7.50"), null);

        final Ingrediente aggiornato = catalogoService.elencaIngredienti().stream()
                .filter(i -> i.getCodiceIngrediente().equals(codice))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingrediente aggiornato non trovato"));

        assertEquals(nome, aggiornato.getNome(), "Il nome non doveva cambiare (parametro null)");
        assertTrue(!aggiornato.isVegano(), "Vegano non doveva cambiare (parametro null)");
        assertEquals(0, new BigDecimal("7.50").compareTo(aggiornato.getPrezzo()));
    }

    // A3 - Inserimento categoria, poi verifica che una seconda chiamata con
    // lo stesso Nome aggiorni la Descrizione invece di creare un duplicato
    // (ON DUPLICATE KEY UPDATE).
    @Test
    void inserisceEAggiornaCategoriaConStessoNome() throws Exception {
        final String nome = "CategoriaTest" + System.currentTimeMillis();

        final long primoCodice = amministrazioneService.inserisciOAggiornaCategoria(nome, "Descrizione originale");
        categorieDaRipulire.add(primoCodice);

        final long secondoCodice = amministrazioneService.inserisciOAggiornaCategoria(nome, "Descrizione aggiornata");

        assertEquals(primoCodice, secondoCodice,
                "Una seconda chiamata con lo stesso Nome deve aggiornare la riga esistente, non crearne una nuova");
    }

    // A4 - Lancio promozione.
    @Test
    void lanciaPromozioneCorrettamente() throws Exception {
        final String nome = "PromoTest" + System.currentTimeMillis();

        final long codice = amministrazioneService.lanciaPromozione(
                nome, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        promozioniDaRipulire.add(codice);

        assertTrue(codice > 0, "Il codice generato deve essere positivo");
    }

    // A5 - Pubblicazione sconto: richiede una categoria e una promozione
    // esistenti, quindi le creiamo apposta per questo test.
    @Test
    void pubblicaScontoCorrettamente() throws Exception {
        final long codiceCategoria = amministrazioneService.inserisciOAggiornaCategoria(
                "CategoriaScontoTest" + System.currentTimeMillis(), "Per test A5");
        categorieDaRipulire.add(codiceCategoria);

        final long codicePromo = amministrazioneService.lanciaPromozione(
                "PromoScontoTest" + System.currentTimeMillis(),
                LocalDate.of(2027, 2, 1), LocalDate.of(2027, 2, 28));
        promozioniDaRipulire.add(codicePromo);

        amministrazioneService.pubblicaSconto(codiceCategoria, codicePromo, 2, 5, new BigDecimal("15.00"));
    }

    // A6 - Costruisce lo scenario: un utente con 3 ricette "Vegano" contenenti
    // un ingrediente non vegano (deve essere bloccato) e un utente di controllo
    // con ricette "Vegano" ma solo ingredienti vegani (deve restare attivo).
    // Nota: nessuna delle 6 ricette demo combina categoria "Vegano" con un
    // ingrediente non vegano, quindi non ci sono falsi positivi dai dati demo.
    @Test
    void rimuoviUtentiIncoerentiBloccaSoloUtentiConTreRicetteIncoerenti() throws Exception {
        final long codiceCategoriaVegano = trovaCodiceCategoria("Vegano");
        final long codicePettoPollo = trovaCodiceIngrediente("Petto di pollo"); // Vegano = FALSE
        final long codicePatate = trovaCodiceIngrediente("Patate pasta gialla"); // Vegano = TRUE

        final long utenteDaBloccare = registraUtenteDiTest();
        for (int i = 0; i < 3; i++) {
            final long codiceRicetta = ricettaService.pubblicaRicetta(
                    "RicettaIncoerente" + i + "_" + System.nanoTime(), utenteDaBloccare,
                    "Preparazione di prova", 10,
                    Map.of(codicePettoPollo, new BigDecimal("100")),
                    List.of(codiceCategoriaVegano));
            ricetteDaRipulire.add(codiceRicetta);
        }

        final long utenteControllo = registraUtenteDiTest();
        for (int i = 0; i < 3; i++) {
            final long codiceRicetta = ricettaService.pubblicaRicetta(
                    "RicettaCoerente" + i + "_" + System.nanoTime(), utenteControllo,
                    "Preparazione di prova", 10,
                    Map.of(codicePatate, new BigDecimal("100")),
                    List.of(codiceCategoriaVegano));
            ricetteDaRipulire.add(codiceRicetta);
        }

        amministrazioneService.rimuoviUtentiIncoerenti();

        assertFalse(isAttivo(utenteDaBloccare), "L'utente con 3 ricette incoerenti deve risultare disattivato");
        assertTrue(isAttivo(utenteControllo), "L'utente di controllo, coerente, non deve essere toccato");

        final boolean ricetteBloccateVisibili = ricettaService.elencaRicette().stream()
                .anyMatch(r -> r.getNomeRicetta().startsWith("RicettaIncoerente"));
        assertFalse(ricetteBloccateVisibili,
                "Le ricette dell'utente bloccato devono risultare rimosse (Rimossa = TRUE)");

        final boolean ricetteControlloVisibili = ricettaService.elencaRicette().stream()
                .anyMatch(r -> r.getNomeRicetta().startsWith("RicettaCoerente"));
        assertTrue(ricetteControlloVisibili,
                "Le ricette dell'utente di controllo devono restare visibili");
    }

    private long registraUtenteDiTest() throws Exception {
        final String email = "test.a6." + System.nanoTime() + "@example.com";
        final long codiceUtente = autenticazioneService.registraUtente(
                "Test", "A6", email, "password", "Via di Prova 1, Bologna");
        utentiDaRipulire.add(codiceUtente);
        return codiceUtente;
    }

    private long trovaCodiceCategoria(final String nome) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT CodiceCategoria FROM CATEGORIE WHERE Nome = ?")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private long trovaCodiceIngrediente(final String nome) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT CodiceIngrediente FROM INGREDIENTI WHERE Nome = ?")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private boolean isAttivo(final long codiceUtente) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT Attivo FROM UTENTI WHERE CodiceUtente = ?")) {
            ps.setLong(1, codiceUtente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }
}