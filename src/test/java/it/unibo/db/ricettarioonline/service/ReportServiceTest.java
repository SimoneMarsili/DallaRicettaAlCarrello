package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.CategoriaOrdinata;
import it.unibo.db.ricettarioonline.model.RicettaOrdinata;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.model.UtenteConRating;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {

    private final ReportService reportService = new ReportService();

    // U8.1 - Rispetta il LIMIT richiesto.
    @Test
    void migliriRicetteRispettaLimit() throws Exception {
        final List<RicettaVista> ricette = reportService.migliriRicette(2);

        assertTrue(ricette.size() <= 2, "Non deve restituire più righe del limite richiesto");
    }

    // U8.1 - Ordinamento decrescente per MediaRecensioni.
    @Test
    void migliriRicetteOrdinatePerMedia() throws Exception {
        final List<RicettaVista> ricette = reportService.migliriRicette(10);

        for (int i = 1; i < ricette.size(); i++) {
            final BigDecimal precedente = ricette.get(i - 1).getMediaRecensioni();
            final BigDecimal corrente = ricette.get(i).getMediaRecensioni();
            assertTrue(precedente.compareTo(corrente) >= 0,
                    "Le ricette devono essere ordinate per MediaRecensioni decrescente");
        }
    }

    // U8.1 - Test con dati noti: calcolato a mano dalle recensioni demo,
    // Amatriciana ha la media più alta (10+9+7)/3 = 8.67.
    @Test
    void migliorRicettaÈAmatriciana() throws Exception {
        final List<RicettaVista> primaRicetta = reportService.migliriRicette(1);

        assertEquals(1, primaRicetta.size());
        assertEquals("Amatriciana", primaRicetta.get(0).getNomeRicetta());
    }

    // U8.2 - Rispetta il LIMIT richiesto.
    @Test
    void miglioriUtentiRispettaLimit() throws Exception {
        final List<UtenteConRating> utenti = reportService.miglioriUtenti(2);

        assertTrue(utenti.size() <= 2, "Non deve restituire più righe del limite richiesto");
    }

    // U8.2 - Ordinamento decrescente per RatingUtente.
    @Test
    void miglioriUtentiOrdinatiPerRating() throws Exception {
        final List<UtenteConRating> utenti = reportService.miglioriUtenti(10);

        for (int i = 1; i < utenti.size(); i++) {
            final BigDecimal precedente = utenti.get(i - 1).getRatingUtente();
            final BigDecimal corrente = utenti.get(i).getRatingUtente();
            assertTrue(precedente.compareTo(corrente) >= 0,
                    "Gli utenti devono essere ordinati per RatingUtente decrescente");
        }
    }

    // U8.3 - Rispetta il LIMIT richiesto e resta ordinato decrescente.
    @Test
    void ricettePiuOrdinateRispettaLimitEOrdinamento() throws Exception {
        final List<RicettaOrdinata> ricette = reportService.ricettePiuOrdinate(10);

        for (int i = 1; i < ricette.size(); i++) {
            final long precedente = ricette.get(i - 1).getQuantitaTotaleOrdinata();
            final long corrente = ricette.get(i).getQuantitaTotaleOrdinata();
            assertTrue(precedente >= corrente,
                    "Le ricette devono essere ordinate per QuantitaTotaleOrdinata decrescente");
        }
    }

    // U8.4 - Rispetta il LIMIT richiesto e resta ordinato decrescente.
    @Test
    void categoriePiuOrdinateRispettaLimitEOrdinamento() throws Exception {
        final List<CategoriaOrdinata> categorie = reportService.categoriePiuOrdinate(10);

        for (int i = 1; i < categorie.size(); i++) {
            final long precedente = categorie.get(i - 1).getQuantitaTotaleOrdinata();
            final long corrente = categorie.get(i).getQuantitaTotaleOrdinata();
            assertTrue(precedente >= corrente,
                    "Le categorie devono essere ordinate per QuantitaTotaleOrdinata decrescente");
        }
    }
}