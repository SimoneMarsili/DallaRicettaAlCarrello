package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.model.RigaStoricoOrdine;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdineServiceTest {

    private final OrdineService ordineService = new OrdineService();
    private final AutenticazioneService autenticazioneService = new AutenticazioneService();
    private final RicettaService ricettaService = new RicettaService();

    // Ordine di pulizia: prima ORDINI (CASCADE su DETTAGLI_ORDINE), poi UTENTI
    // (a quel punto liberi da RESTRICT).
    private final List<Long> ordiniDaRipulire = new ArrayList<>();
    private final List<Long> utentiDaRipulire = new ArrayList<>();

    @AfterEach
    void pulisci() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            eliminaTutti(connection, "DELETE FROM ORDINI WHERE CodiceOrdine = ?", ordiniDaRipulire);
            eliminaTutti(connection, "DELETE FROM UTENTI WHERE CodiceUtente = ?", utentiDaRipulire);
        }
        ordiniDaRipulire.clear();
        utentiDaRipulire.clear();
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

    // U9 + U10 - Crea un ordine per una ricetta demo (Carbonara), poi verifica
    // tramite lo storico che la riga sia coerente: stesso prezzo unitario
    // della ricetta al momento dell'ordine, sconto in un range plausibile
    // (0-100, non verifichiamo un valore fisso perché dipende dalla data
    // reale in cui gira il test), e TotaleRiga calcolato correttamente.
    @Test
    void registraOrdineCreaRigaStoricoCoerente() throws Exception {
        final long codiceUtente = registraUtenteDiTestConIndirizzo();
        final RicettaVista carbonara = ricettaService.cercaPerNome("Carbonara").get(0);

        final long codiceOrdine = ordineService.registraOrdine(
                codiceUtente, "Ordine di test", Map.of(carbonara.getCodiceRicetta(), 2));
        ordiniDaRipulire.add(codiceOrdine);

        assertTrue(codiceOrdine > 0, "Il codice ordine generato deve essere positivo");

        final List<RigaStoricoOrdine> storico = ordineService.storicoOrdini(codiceUtente);
        assertEquals(1, storico.size(), "Deve esserci esattamente una riga di dettaglio");

        final RigaStoricoOrdine riga = storico.get(0);
        assertEquals(codiceOrdine, riga.getCodiceOrdine());
        assertEquals("Carbonara", riga.getNomeRicetta());
        assertEquals(2, riga.getQuantita());
        assertEquals(0, carbonara.getPrezzoRicetta().compareTo(riga.getPrezzoUnitario()),
                "Il prezzo unitario salvato deve coincidere col PrezzoRicetta al momento dell'ordine");

        assertTrue(riga.getScontoApplicato().compareTo(BigDecimal.ZERO) >= 0
                        && riga.getScontoApplicato().compareTo(new BigDecimal("100")) <= 0,
                "Lo sconto applicato deve essere tra 0 e 100");

        final BigDecimal totaleAtteso = riga.getPrezzoUnitario()
                .multiply(BigDecimal.valueOf(riga.getQuantita()))
                .multiply(BigDecimal.ONE.subtract(
                        riga.getScontoApplicato().divide(new BigDecimal("100"))))
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(0, totaleAtteso.compareTo(riga.getTotaleRiga()),
                "TotaleRiga deve corrispondere a Quantità * PrezzoUnitario * (1 - Sconto/100)");
    }

    // U9 - Il trigger trg_ordini_verifica_indirizzo deve bloccare la creazione
    // dell'ordine se l'utente non ha un IndirizzoSpedizione impostato.
    @Test
    void registraOrdineFallisceSenzaIndirizzoSpedizione() throws Exception {
        final String email = "test.ordine.noaddr." + System.nanoTime() + "@example.com";
        final long codiceUtente = autenticazioneService.registraUtente(
                "Test", "SenzaIndirizzo", email, "password", null);
        utentiDaRipulire.add(codiceUtente);

        final long codiceRicetta = ricettaService.cercaPerNome("Carbonara").get(0).getCodiceRicetta();

        assertThrows(SQLException.class, () ->
                ordineService.registraOrdine(codiceUtente, "Nota", Map.of(codiceRicetta, 1)));
    }

    // U10 - Un utente appena registrato, senza ordini, deve avere storico vuoto.
    @Test
    void storicoOrdiniVuotoPerUtenteSenzaOrdini() throws Exception {
        final long codiceUtente = registraUtenteDiTestConIndirizzo();

        final List<RigaStoricoOrdine> storico = ordineService.storicoOrdini(codiceUtente);

        assertTrue(storico.isEmpty(), "Un utente senza ordini deve avere storico vuoto");
    }

    private long registraUtenteDiTestConIndirizzo() throws Exception {
        final String email = "test.ordine." + System.nanoTime() + "@example.com";
        final long codiceUtente = autenticazioneService.registraUtente(
                "Test", "Ordine", email, "password", "Via di Prova 1, Bologna");
        utentiDaRipulire.add(codiceUtente);
        return codiceUtente;
    }
}