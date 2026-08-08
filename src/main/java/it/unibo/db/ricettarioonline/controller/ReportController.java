package it.unibo.db.ricettarioonline.controller;

import it.unibo.db.ricettarioonline.model.CategoriaOrdinata;
import it.unibo.db.ricettarioonline.model.RicettaOrdinata;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.model.UtenteConRating;
import it.unibo.db.ricettarioonline.service.ReportService;

import java.sql.SQLException;
import java.util.List;

public class ReportController {

    private final ReportService reportService = new ReportService();

    public List<RicettaVista> migliriRicette(final int limit) throws SQLException {
        return reportService.migliriRicette(limit);
    }

    public List<UtenteConRating> miglioriUtenti(final int limit) throws SQLException {
        return reportService.miglioriUtenti(limit);
    }

    public List<RicettaOrdinata> ricettePiuOrdinate(final int limit) throws SQLException {
        return reportService.ricettePiuOrdinate(limit);
    }

    public List<CategoriaOrdinata> categoriePiuOrdinate(final int limit) throws SQLException {
        return reportService.categoriePiuOrdinate(limit);
    }
}