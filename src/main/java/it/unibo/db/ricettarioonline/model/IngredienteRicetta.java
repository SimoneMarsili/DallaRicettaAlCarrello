package it.unibo.db.ricettarioonline.model;

import java.math.BigDecimal;

// Proiezione di visualizzazione per U11.2: un ingrediente con la sua quantità
// all'interno di una specifica ricetta. Non è un report: solo un JOIN a scopo
// di visualizzazione (mostra il Nome invece del solo CodiceIngrediente).
public class IngredienteRicetta {

    private final long codiceIngrediente;
    private final String nome;
    private final BigDecimal quantita; // grammi

    public IngredienteRicetta(final long codiceIngrediente, final String nome,
                               final BigDecimal quantita) {
        this.codiceIngrediente = codiceIngrediente;
        this.nome = nome;
        this.quantita = quantita;
    }

    public long getCodiceIngrediente() {
        return codiceIngrediente;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getQuantita() {
        return quantita;
    }
}