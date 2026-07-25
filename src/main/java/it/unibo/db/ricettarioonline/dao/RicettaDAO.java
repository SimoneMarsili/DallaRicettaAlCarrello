package it.unibo.db.ricettarioonline.dao;

import it.unibo.db.ricettarioonline.model.Ricetta;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RicettaDAO {

    // U6 (step 1) - Pubblicazione ricetta. Riceve la Connection dal chiamante
    // perché fa parte della stessa transazione di DettagliRicetta/Classificazioni.
    long insert(Ricetta ricetta) throws SQLException;

    // U11.1 - Dettaglio ricetta (prima delle 3 query separate).
    Optional<RicettaVista> findById(long codiceRicetta) throws SQLException;

    // U2 - Visualizzazione ricette.
    List<RicettaVista> findVisibili() throws SQLException;

    // U3.1 - Filtro per nome.
    List<RicettaVista> findByNome(String nome) throws SQLException;

    // U3.2 - Filtro per ingrediente.
    List<RicettaVista> findByIngrediente(String nomeIngrediente) throws SQLException;

    // U3.3 - Filtro per tempo massimo.
    List<RicettaVista> findByTempoMax(int minuti) throws SQLException;

    // U3.4 - Filtro per fascia di prezzo.
    List<RicettaVista> findByPrezzoRange(BigDecimal min, BigDecimal max) throws SQLException;

    // U3.5 - Filtro per autore.
    List<RicettaVista> findByAutore(String nome, String cognome) throws SQLException;

    // U3.6 - Filtro per categoria.
    List<RicettaVista> findByCategoria(String nomeCategoria) throws SQLException;

    // A6 (step 3) - Rimozione logica delle ricette di un gruppo di utenti.
    // Riceve la Connection dal chiamante per condividere la transazione con
    // UtenteDAO.disattivaBatch.
    void rimuoviBatch(List<Long> codiciUtente) throws SQLException;

    // Nota: U8.1 (migliori ricette) e U8.3 (ricette più ordinate) NON stanno qui.
    // Sono report con ORDER BY/GROUP BY a scopo di classifica: vivranno nel
    // service layer di reportistica.
}
