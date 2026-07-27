package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmministrazioneServiceTest {

    private final AmministrazioneService amministrazioneService = new AmministrazioneService();
    private final CatalogoService catalogoService = new CatalogoService();

    // Tracciamo cosa creiamo per ripulirlo dopo, stesso pattern già usato in
    // AutenticazioneServiceTest. L'ordine di cancellazione nel @AfterEach
    // rispetta i vincoli FK: prima SCONTI, poi CATEGORIE/PROMOZIONI (che le
    // referenziano), poi INGREDIENTI (indipendente).
    private final List<Long> ingredientiDaRipulire = new ArrayList<>();
    private final List<Long> categorieDaRipulire = new ArrayList<>();
    private final List<Long> promozioniDaRipulire = new ArrayList<>();

    @AfterEach
    void pulisci() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            eliminaTutti(connection, "DELETE FROM SCONTI WHERE CodiceCategoria = ?", categorieDaRipulire);
            eliminaTutti(connection, "DELETE FROM CATEGORIE WHERE CodiceCategoria = ?", categorieDaRipulire);
            eliminaTutti(connection, "DELETE FROM PROMOZIONI WHERE CodicePromo = ?", promozioniDaRipulire);
            eliminaTutti(connection, "DELETE FROM INGREDIENTI WHERE CodiceIngrediente = ?", ingredientiDaRipulire);
        }
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

        // Non lancia eccezioni = successo: ScontoDAO non ha un metodo di
        // lettura pubblico nel service, quindi qui verifichiamo solo che
        // l'operazione vada a buon fine senza violare i vincoli (CHECK,
        // trigger anti-sovrapposizione).
        amministrazioneService.pubblicaSconto(codiceCategoria, codicePromo, 2, 5, new BigDecimal("15.00"));
    }
}