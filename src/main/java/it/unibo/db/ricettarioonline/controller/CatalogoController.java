package it.unibo.db.ricettarioonline.controller;

import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.VantaggioAttivo;
import it.unibo.db.ricettarioonline.service.CatalogoService;

import java.sql.SQLException;
import java.util.List;

public class CatalogoController {

    private final CatalogoService catalogoService = new CatalogoService();

    public List<Ingrediente> elencaIngredienti() throws SQLException {
        return catalogoService.elencaIngredienti();
    }

    public List<Categoria> elencaCategorie() throws SQLException {
        return catalogoService.elencaCategorie();
    }

    public List<VantaggioAttivo> vantaggiAttivi() throws SQLException {
        return catalogoService.vantaggiAttivi();
    }
}