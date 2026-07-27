package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.dao.DettaglioOrdineDAO;
import it.unibo.db.ricettarioonline.dao.JdbcDettaglioOrdineDAO;
import it.unibo.db.ricettarioonline.dao.JdbcOrdineDAO;
import it.unibo.db.ricettarioonline.dao.JdbcRicettaDAO;
import it.unibo.db.ricettarioonline.dao.JdbcScontoDAO;
import it.unibo.db.ricettarioonline.dao.OrdineDAO;
import it.unibo.db.ricettarioonline.dao.RicettaDAO;
import it.unibo.db.ricettarioonline.dao.ScontoDAO;
import it.unibo.db.ricettarioonline.model.DettaglioOrdine;
import it.unibo.db.ricettarioonline.model.Ordine;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.model.RigaStoricoOrdine;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrdineService {

    // U9 - Creazione ordine: 1 INSERT su ORDINI, poi per ciascuna delle j
    // ricette nel carrello si determina prezzo e sconto migliore e si
    // inserisce la riga di dettaglio corrispondente. Tutto nella stessa
    // transazione. Ritorna il codice dell'ordine creato.
    public long registraOrdine(final long codiceUtente, final String note,
            final Map<Long, Integer> carrello) throws SQLException {

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Quattro DAO, stessa Connection: fanno parte della stessa
            // transazione perché condividono lo stesso oggetto Connection.
            final OrdineDAO ordineDAO = new JdbcOrdineDAO(connection);
            final RicettaDAO ricettaDAO = new JdbcRicettaDAO(connection);
            final ScontoDAO scontoDAO = new JdbcScontoDAO(connection);
            final DettaglioOrdineDAO dettaglioOrdineDAO = new JdbcDettaglioOrdineDAO(connection);

            // Il trigger trg_ordini_verifica_indirizzo, lato database, blocca
            // questo INSERT se l'utente non ha un IndirizzoSpedizione impostato.
            final Ordine nuovoOrdine = new Ordine(note, codiceUtente);
            final long codiceOrdine = ordineDAO.insert(nuovoOrdine);

            // Loop j volte: una riga di dettaglio per ogni ricetta nel carrello.
            for (final Map.Entry<Long, Integer> voce : carrello.entrySet()) {
                final long codiceRicetta = voce.getKey();
                final int quantita = voce.getValue();

                final Optional<RicettaVista> ricetta = ricettaDAO.findById(codiceRicetta);
                if (ricetta.isEmpty()) {
                    throw new SQLException("Ricetta non trovata o non più disponibile: " + codiceRicetta);
                }

                final BigDecimal prezzoUnitario = ricetta.get().getPrezzoRicetta();
                final BigDecimal migliorSconto = scontoDAO.findMigliorSconto(codiceRicetta);

                final DettaglioOrdine dettaglio = new DettaglioOrdine(
                        codiceOrdine, codiceRicetta, prezzoUnitario, quantita, migliorSconto);
                dettaglioOrdineDAO.insert(dettaglio);
            }

            connection.commit();
            return codiceOrdine;

        } catch (final SQLException ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw ex;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    // U10 - Storico ordini di un utente.
    public List<RigaStoricoOrdine> storicoOrdini(final long codiceUtente) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final OrdineDAO ordineDAO = new JdbcOrdineDAO(connection);
            return ordineDAO.findStoricoByUtente(codiceUtente);
        }
    }
}
