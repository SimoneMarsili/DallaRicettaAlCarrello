package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.VantaggioAttivo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogoServiceTest {

    private static final String TEST_INGREDIENTE = "Spaghetti";
    private final CatalogoService catalogoService = new CatalogoService();

    // U4 - Lo script di popolamento inserisce 15 ingredienti: la lista non
    // deve mai essere vuota su un DB popolato correttamente.
    @Test
    void elencoIngredientiNonVuoto() throws Exception {
        final List<Ingrediente> ingredienti = catalogoService.elencaIngredienti();

        assertFalse(ingredienti.isEmpty(),
                "L'elenco ingredienti non deve essere vuoto su un DB popolato");
    }

    // U4 - Verifica che l'ORDER BY Nome funzioni davvero: la lista deve
    // arrivare già in ordine alfabetico.
    @Test
    void ingredientiOrdinatiPerNome() throws Exception {
        final List<Ingrediente> ingredienti = catalogoService.elencaIngredienti();

        for (int i = 1; i < ingredienti.size(); i++) {
            final String precedente = ingredienti.get(i - 1).getNome();
            final String corrente = ingredienti.get(i).getNome();
            assertTrue(precedente.compareToIgnoreCase(corrente) <= 0,
                    "Gli ingredienti devono essere ordinati per nome: '"
                            + precedente + "' dovrebbe precedere '" + corrente + "'");
        }
    }

    // U4 - Test con dati noti: verifica che un ingrediente specifico dello
    // script di popolamento abbia esattamente i valori attesi. A differenza
    // dei due test sopra, questo cattura bug di mapping (es. colonne
    // scambiate, Prezzo non selezionato).
    @Test
    void ingredienteDiTestPresenteConDatiCorretti() throws Exception {
        final List<Ingrediente> ingredienti = catalogoService.elencaIngredienti();

        final Ingrediente spaghetti = ingredienti.stream()
                .filter(i -> TEST_INGREDIENTE.equals(i.getNome()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Ingrediente '" + TEST_INGREDIENTE + "' non trovato: DB popolato correttamente?"));

        assertTrue(spaghetti.isVegano(), "Spaghetti dovrebbe essere vegano secondo i dati demo");
        assertEquals(0, new BigDecimal("2.50").compareTo(spaghetti.getPrezzo()),
                "Il prezzo di Spaghetti dovrebbe essere 2.50");
    }

    // U7 - Non verifichiamo un valore specifico (la finestra delle promozioni
    // demo potrebbe non essere attiva alla data in cui gira il test):
    // verifichiamo solo che la query giri senza eccezioni e che ogni riga
    // abbia una percentuale di sconto plausibile.
    @Test
    void vantaggiAttiviNonLanciaEccezioni() throws Exception {
        final List<VantaggioAttivo> vantaggi = catalogoService.vantaggiAttivi();

        for (final VantaggioAttivo vantaggio : vantaggi) {
            assertTrue(vantaggio.getPercentualeSconto().signum() >= 0,
                    "La percentuale di sconto non può essere negativa");
        }
    }
}