package it.unibo.db.ricettarioonline.controller;

import it.unibo.db.ricettarioonline.model.RigaStoricoOrdine;
import it.unibo.db.ricettarioonline.service.OrdineService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrdineController {

    private final OrdineService ordineService = new OrdineService();

    public long registraOrdine(final long codiceUtente, final String note,
            final Map<Long, Integer> carrello) throws SQLException {
        return ordineService.registraOrdine(codiceUtente, note, carrello);
    }

    public List<RigaStoricoOrdine> storicoOrdini(final long codiceUtente) throws SQLException {
        return ordineService.storicoOrdini(codiceUtente);
    }
}