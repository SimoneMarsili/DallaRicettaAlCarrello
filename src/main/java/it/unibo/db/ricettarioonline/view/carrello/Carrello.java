package it.unibo.db.ricettarioonline.view.carrello;

import it.unibo.db.ricettarioonline.model.RicettaVista;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Stato del carrello, condiviso tra CatalogoPanel (dove si aggiunge) e
// CarrelloPanel (dove si vede/conferma). Vive solo in memoria: non è
// persistito, si svuota alla chiusura dell'app o dopo la conferma di un ordine.
public class Carrello {

    // Una riga del carrello: la ricetta (per nome/prezzo di visualizzazione)
    // e la quantità scelta.
    public static class Riga {
        private final RicettaVista ricetta;
        private final int quantita;

        private Riga(final RicettaVista ricetta, final int quantita) {
            this.ricetta = ricetta;
            this.quantita = quantita;
        }

        public RicettaVista getRicetta() {
            return ricetta;
        }

        public int getQuantita() {
            return quantita;
        }
    }

    @FunctionalInterface
    public interface Listener {
        void carrelloAggiornato();
    }

    // Chiave = CodiceRicetta, non l'oggetto RicettaVista: RicettaVista non
    // ridefinisce equals/hashCode, quindi due istanze diverse per la stessa
    // ricetta (es. caricate in momenti diversi) andrebbero trattate come
    // chiavi diverse se usassi l'oggetto stesso.
    private final Map<Long, Riga> righe = new LinkedHashMap<>();
    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    public void aggiungi(final RicettaVista ricetta, final int quantita) {
        final Riga esistente = righe.get(ricetta.getCodiceRicetta());
        final int nuovaQuantita = (esistente == null ? 0 : esistente.getQuantita()) + quantita;
        righe.put(ricetta.getCodiceRicetta(), new Riga(ricetta, nuovaQuantita));
        notificaAscoltatori();
    }

    public void rimuovi(final long codiceRicetta) {
        righe.remove(codiceRicetta);
        notificaAscoltatori();
    }

    public void svuota() {
        righe.clear();
        notificaAscoltatori();
    }

    public List<Riga> getRighe() {
        return Collections.unmodifiableList(new ArrayList<>(righe.values()));
    }

    // Per OrdineController.registraOrdine, che vuole Map<Long, Integer>.
    public Map<Long, Integer> toMappaQuantita() {
        final Map<Long, Integer> mappa = new LinkedHashMap<>();
        righe.forEach((codice, riga) -> mappa.put(codice, riga.getQuantita()));
        return mappa;
    }

    public boolean isEmpty() {
        return righe.isEmpty();
    }

    private void notificaAscoltatori() {
        listeners.forEach(Listener::carrelloAggiornato);
    }
}