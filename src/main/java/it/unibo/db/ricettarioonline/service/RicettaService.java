package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.dao.ClassificazioneDAO;
import it.unibo.db.ricettarioonline.dao.DettaglioRicettaDAO;
import it.unibo.db.ricettarioonline.dao.JdbcClassificazioneDAO;
import it.unibo.db.ricettarioonline.dao.JdbcDettaglioRicettaDAO;
import it.unibo.db.ricettarioonline.dao.JdbcRecensioneDAO;
import it.unibo.db.ricettarioonline.dao.JdbcRicettaDAO;
import it.unibo.db.ricettarioonline.dao.RecensioneDAO;
import it.unibo.db.ricettarioonline.dao.RicettaDAO;
import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.Classificazione;
import it.unibo.db.ricettarioonline.model.DettaglioRicetta;
import it.unibo.db.ricettarioonline.model.IngredienteRicetta;
import it.unibo.db.ricettarioonline.model.Recensione;
import it.unibo.db.ricettarioonline.model.Ricetta;
import it.unibo.db.ricettarioonline.model.RicettaCompleta;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RicettaService {

    // U6 - Pubblicazione ricetta: 1 INSERT su RICETTE, poi n INSERT su
    // DETTAGLI_RICETTA e k INSERT su CLASSIFICAZIONI, tutto nella stessa
    // transazione. Ritorna il codice della ricetta creata.
    public long pubblicaRicetta(final String nome, final long codiceUtente,
            final String preparazione, final int tempoRichiesto,
            final Map<Long, BigDecimal> ingredienti, final List<Long> codiciCategoria) throws SQLException {

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Tre DAO, stessa Connection: fanno parte della stessa transazione
            // perché condividono lo stesso oggetto Connection.
            final RicettaDAO ricettaDAO = new JdbcRicettaDAO(connection);
            final DettaglioRicettaDAO dettaglioRicettaDAO = new JdbcDettaglioRicettaDAO(connection);
            final ClassificazioneDAO classificazioneDAO = new JdbcClassificazioneDAO(connection);

            final Ricetta nuovaRicetta = new Ricetta(nome, codiceUtente, preparazione, tempoRichiesto);
            final long codiceRicetta = ricettaDAO.insert(nuovaRicetta);

            // Loop n volte: una INSERT per ogni ingrediente selezionato.
            // NumeroIngredienti/PrezzoRicetta si aggiornano da soli via trigger
            // ad ogni singolo INSERT su DETTAGLI_RICETTA.
            for (final Map.Entry<Long, BigDecimal> ingrediente : ingredienti.entrySet()) {
                final DettaglioRicetta dettaglio = new DettaglioRicetta(
                        codiceRicetta, ingrediente.getKey(), ingrediente.getValue());
                dettaglioRicettaDAO.insert(dettaglio);
            }

            // Loop k volte: una INSERT per ogni categoria selezionata.
            for (final Long codiceCategoria : codiciCategoria) {
                final Classificazione classificazione = new Classificazione(codiceRicetta, codiceCategoria);
                classificazioneDAO.insert(classificazione);
            }

            connection.commit();
            return codiceRicetta;

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

    // U5 - Pubblicazione recensione. Singola operazione, nessuna transazione
    // multi-step: il trigger su RECENSIONI aggiorna da solo MediaRecensioni.
    public void pubblicaRecensione(final long codiceUtente, final long codiceRicetta,
            final int voto, final String commento) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final RecensioneDAO recensioneDAO = new JdbcRecensioneDAO(connection);
            final Recensione recensione = new Recensione(codiceUtente, codiceRicetta, voto, commento);
            recensioneDAO.insert(recensione);
        }
    }

    // U11 - Dettaglio ricetta: assembla le 3 query separate (RicettaDAO,
    // DettaglioRicettaDAO, ClassificazioneDAO) in un solo oggetto.
    // Nessuna transazione: solo letture, Connection condivisa solo per
    // evitare di aprirne tre separate, auto-commit di default.
    public Optional<RicettaCompleta> getDettaglioRicetta(final long codiceRicetta) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final RicettaDAO ricettaDAO = new JdbcRicettaDAO(connection);
            final DettaglioRicettaDAO dettaglioRicettaDAO = new JdbcDettaglioRicettaDAO(connection);
            final ClassificazioneDAO classificazioneDAO = new JdbcClassificazioneDAO(connection);

            final Optional<RicettaVista> ricetta = ricettaDAO.findById(codiceRicetta);
            if (ricetta.isEmpty()) {
                return Optional.empty();
            }

            final List<IngredienteRicetta> ingredienti = dettaglioRicettaDAO.findByRicetta(codiceRicetta);
            final List<Categoria> categorie = classificazioneDAO.findByRicetta(codiceRicetta);

            return Optional.of(new RicettaCompleta(ricetta.get(), ingredienti, categorie));
        }
    }

    // U2 - Visualizzazione ricette.
    public List<RicettaVista> elencaRicette() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findVisibili();
        }
    }

    // U3.1 - Filtro per nome.
    public List<RicettaVista> cercaPerNome(final String nome) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findByNome(nome);
        }
    }

    // U3.2 - Filtro per ingrediente.
    public List<RicettaVista> cercaPerIngrediente(final String nomeIngrediente) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findByIngrediente(nomeIngrediente);
        }
    }

    // U3.3 - Filtro per tempo massimo.
    public List<RicettaVista> cercaPerTempoMax(final int minuti) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findByTempoMax(minuti);
        }
    }

    // U3.4 - Filtro per fascia di prezzo.
    public List<RicettaVista> cercaPerPrezzo(final BigDecimal min, final BigDecimal max) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findByPrezzoRange(min, max);
        }
    }

    // U3.5 - Filtro per autore.
    public List<RicettaVista> cercaPerAutore(final String nome, final String cognome) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findByAutore(nome, cognome);
        }
    }

    // U3.6 - Filtro per categoria.
    public List<RicettaVista> cercaPerCategoria(final String nomeCategoria) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return new JdbcRicettaDAO(connection).findByCategoria(nomeCategoria);
        }
    }
}