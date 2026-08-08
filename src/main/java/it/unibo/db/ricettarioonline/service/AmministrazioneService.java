package it.unibo.db.ricettarioonline.service;

import it.unibo.db.ricettarioonline.dao.CategoriaDAO;
import it.unibo.db.ricettarioonline.dao.IngredienteDAO;
import it.unibo.db.ricettarioonline.dao.JdbcCategoriaDAO;
import it.unibo.db.ricettarioonline.dao.JdbcIngredienteDAO;
import it.unibo.db.ricettarioonline.dao.JdbcPromozioneDAO;
import it.unibo.db.ricettarioonline.dao.JdbcRicettaDAO;
import it.unibo.db.ricettarioonline.dao.JdbcScontoDAO;
import it.unibo.db.ricettarioonline.dao.JdbcUtenteDAO;
import it.unibo.db.ricettarioonline.dao.PromozioneDAO;
import it.unibo.db.ricettarioonline.dao.RicettaDAO;
import it.unibo.db.ricettarioonline.dao.ScontoDAO;
import it.unibo.db.ricettarioonline.dao.UtenteDAO;
import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.FatturatoGiornaliero;
import it.unibo.db.ricettarioonline.model.FatturatoRicetta;
import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.Promozione;
import it.unibo.db.ricettarioonline.model.RecensioneNegativa;
import it.unibo.db.ricettarioonline.model.Sconto;
import it.unibo.db.ricettarioonline.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AmministrazioneService {

    // A1 - Inserimento ingrediente.
    public long inserisciIngrediente(final String nome, final BigDecimal prezzo,
            final boolean vegano) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final IngredienteDAO ingredienteDAO = new JdbcIngredienteDAO(connection);
            final Ingrediente nuovoIngrediente = new Ingrediente(nome, prezzo, vegano);
            return ingredienteDAO.insert(nuovoIngrediente);
        }
    }

    // A2 - Aggiornamento parziale di un ingrediente esistente.
    public void aggiornaIngrediente(final long codiceIngrediente, final String nome,
            final BigDecimal prezzo, final Boolean vegano) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final IngredienteDAO ingredienteDAO = new JdbcIngredienteDAO(connection);
            ingredienteDAO.update(codiceIngrediente, nome, prezzo, vegano);
        }
    }

    // A3 - Inserimento categoria, o aggiornamento della descrizione se il Nome esiste già.
    public long inserisciOAggiornaCategoria(final String nome, final String descrizione) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final CategoriaDAO categoriaDAO = new JdbcCategoriaDAO(connection);
            final Categoria categoria = new Categoria(nome, descrizione);
            return categoriaDAO.insertOrUpdate(categoria);
        }
    }

    // A4 - Lancio promozione.
    public long lanciaPromozione(final String nome, final LocalDate dataInizio,
            final LocalDate dataFine) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final PromozioneDAO promozioneDAO = new JdbcPromozioneDAO(connection);
            final Promozione promozione = new Promozione(nome, dataInizio, dataFine);
            return promozioneDAO.insert(promozione);
        }
    }

    // A5 - Pubblicazione sconto.
    public void pubblicaSconto(final long codiceCategoria, final long codicePromo,
            final int minIngredienti, final int maxIngredienti,
            final BigDecimal percentualeSconto) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            final ScontoDAO scontoDAO = new JdbcScontoDAO(connection);
            final Sconto sconto = new Sconto(codiceCategoria, codicePromo, minIngredienti,
                    maxIngredienti, percentualeSconto);
            scontoDAO.insertOrUpdate(sconto);
        }
    }

    // A6 - Rimozione utenti (batch): identifica gli utenti con almeno 3
    // ricette che dichiarano la categoria "Vegano" ma contengono ingredienti
    // non vegani, poi li disattiva e rimuove (logicamente) le loro ricette,
    // tutto nella stessa transazione.
    public void rimuoviUtentiIncoerenti() throws SQLException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            final List<Long> codiciUtenteDaBloccare = identificaUtentiIncoerenti(connection);

            final UtenteDAO utenteDAO = new JdbcUtenteDAO(connection);
            final RicettaDAO ricettaDAO = new JdbcRicettaDAO(connection);

            utenteDAO.disattivaBatch(codiciUtenteDaBloccare);
            ricettaDAO.rimuoviBatch(codiciUtenteDaBloccare);

            connection.commit();

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

    // Step 1 di A6: identifica gli utenti con almeno 3 ricette classificate
    // come "Vegano" che però contengono almeno un ingrediente non vegano.
    // A differenza dello script SQL originale, non serve una CREATE TEMPORARY
    // TABLE: il risultato viene letto direttamente in una List<Long> Java,
    // quindi l'UPDATE successivo (fatto dai DAO) non referenzia mai nella
    // stessa istruzione la tabella che sta modificando (niente errore 1093).
    private List<Long> identificaUtentiIncoerenti(final Connection connection) throws SQLException {
        final String sql = "SELECT R.CodiceUtente "
                + "FROM RICETTE R "
                + "JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta "
                + "JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria AND C.Nome = 'Vegano' "
                + "WHERE EXISTS ("
                + "    SELECT 1 FROM DETTAGLI_RICETTA DR "
                + "    JOIN INGREDIENTI I ON I.CodiceIngrediente = DR.CodiceIngrediente "
                + "    WHERE DR.CodiceRicetta = R.CodiceRicetta AND I.Vegano = FALSE"
                + ") "
                + "GROUP BY R.CodiceUtente "
                + "HAVING COUNT(DISTINCT R.CodiceRicetta) >= 3";

        final List<Long> risultato = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risultato.add(rs.getLong("CodiceUtente"));
            }
        }

        return risultato;
    }


    // Supporto ad A5 - Elenco promozioni esistenti, per il selettore nel form.
    public List<Promozione> elencaPromozioni() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final PromozioneDAO promozioneDAO = new JdbcPromozioneDAO(connection);
            return promozioneDAO.findAll();
        }
    }

    // Vista v_fatturato_giornaliero - SQL diretto, come per i report U8.x:
    // legge direttamente dalla vista definita nello schema fisico.
    public List<FatturatoGiornaliero> fatturatoGiornaliero() throws SQLException {
        final String sql = "SELECT Data, NumeroOrdini, QuantitaTotaleVenduta, IncassoTotale "
                + "FROM v_fatturato_giornaliero";

        final List<FatturatoGiornaliero> risultato = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risultato.add(new FatturatoGiornaliero(
                        rs.getObject("Data", LocalDate.class),
                        rs.getInt("NumeroOrdini"),
                        rs.getLong("QuantitaTotaleVenduta"),
                        rs.getBigDecimal("IncassoTotale")
                ));
            }
        }
        return risultato;
    }

    // Vista v_fatturato_per_ricetta.
    public List<FatturatoRicetta> fatturatoPerRicetta() throws SQLException {
        final String sql = "SELECT CodiceRicetta, NomeRicetta, NumeroOrdini, QuantitaTotaleVenduta, IncassoTotale "
                + "FROM v_fatturato_per_ricetta";

        final List<FatturatoRicetta> risultato = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risultato.add(new FatturatoRicetta(
                        rs.getLong("CodiceRicetta"),
                        rs.getString("NomeRicetta"),
                        rs.getInt("NumeroOrdini"),
                        rs.getLong("QuantitaTotaleVenduta"),
                        rs.getBigDecimal("IncassoTotale")
                ));
            }
        }
        return risultato;
    }

    // Vista v_recensioni_negative_recenti.
    public List<RecensioneNegativa> recensioniNegativeRecenti() throws SQLException {
        final String sql = "SELECT CodiceRicetta, NomeRicetta, CodiceUtente, NomeUtente, CognomeUtente, "
                + "Voto, Commento, Data FROM v_recensioni_negative_recenti";

        final List<RecensioneNegativa> risultato = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risultato.add(new RecensioneNegativa(
                        rs.getLong("CodiceRicetta"),
                        rs.getString("NomeRicetta"),
                        rs.getLong("CodiceUtente"),
                        rs.getString("NomeUtente"),
                        rs.getString("CognomeUtente"),
                        rs.getInt("Voto"),
                        rs.getString("Commento"),
                        rs.getObject("Data", LocalDate.class)
                ));
            }
        }
        return risultato;
    }
}