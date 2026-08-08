package it.unibo.db.ricettarioonline.view.catalogo;

import it.unibo.db.ricettarioonline.controller.RicettaController;
import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.IngredienteRicetta;
import it.unibo.db.ricettarioonline.model.RicettaCompleta;
import it.unibo.db.ricettarioonline.model.RicettaVista;
import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.view.carrello.Carrello;
import it.unibo.db.ricettarioonline.view.components.RoundedCardPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

// U11 + U5 + U9 (preparazione) - Dettaglio completo di una ricetta:
// categorie, ingredienti, preparazione, form recensione, form aggiungi al
// carrello. Riusabile da qualunque schermata che sappia già mostrare un
// elenco di ricette (CatalogoPanel, ClassifichePanel, ...): riceve il
// dettaglio già caricato e un callback "onIndietro" per tornare alla
// schermata chiamante, di cui non ha bisogno di sapere altro.
public class RicettaDettaglioPanel extends JPanel {

    private final RicettaController ricettaController = new RicettaController();
    private final Utente utenteCorrente;
    private final Carrello carrello;

    public RicettaDettaglioPanel(final Utente utenteCorrente, final Carrello carrello,
            final RicettaCompleta dettaglio, final Runnable onIndietro) {
        super(new BorderLayout(0, 16));
        this.utenteCorrente = utenteCorrente;
        this.carrello = carrello;
        setOpaque(false);

        final JButton indietroButton = new JButton("← Indietro");
        indietroButton.setBorderPainted(false);
        indietroButton.setContentAreaFilled(false);
        indietroButton.setForeground(AppTheme.PRIMARY);
        indietroButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        indietroButton.addActionListener(e -> onIndietro.run());

        final JLabel titolo = new JLabel(dettaglio.getRicetta().getNomeRicetta());
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.TEXT);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JLabel sottotitolo = new JLabel(String.format(
                "di %s %s · %d min · %.2f € · voto medio %.1f/10",
                dettaglio.getRicetta().getNomeAutore(), dettaglio.getRicetta().getCognomeAutore(),
                dettaglio.getRicetta().getTempoRichiesto(), dettaglio.getRicetta().getPrezzoRicetta(),
                dettaglio.getRicetta().getMediaRecensioni()));
        sottotitolo.setFont(AppTheme.FONT_SUBTITLE);
        sottotitolo.setForeground(AppTheme.TEXT_MUTED);
        sottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(indietroButton);
        header.add(titolo);
        header.add(sottotitolo);
        header.add(Box.createVerticalStrut(6));
        header.add(creaChipCategorie(dettaglio.getCategorie()));
        add(header, BorderLayout.NORTH);

        final JPanel corpo = new JPanel(new BorderLayout(0, 20));
        corpo.setOpaque(false);
        corpo.add(creaSezioneContenuto(dettaglio), BorderLayout.CENTER);

        final JPanel rigaAzioni = new JPanel(new BorderLayout(16, 0));
        rigaAzioni.setOpaque(false);
        rigaAzioni.add(creaCardAggiungiCarrello(dettaglio.getRicetta()), BorderLayout.WEST);
        rigaAzioni.add(creaCardRecensione(dettaglio.getRicetta().getCodiceRicetta()), BorderLayout.EAST);
        corpo.add(rigaAzioni, BorderLayout.SOUTH);

        final JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel creaChipCategorie(final List<Categoria> categorie) {
        final JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        riga.setOpaque(false);

        for (final Categoria categoria : categorie) {
            final JLabel chip = new JLabel(categoria.getNome());
            chip.setOpaque(true);
            chip.setBackground(AppTheme.SURFACE_MUTED);
            chip.setForeground(AppTheme.ACCENT);
            chip.setFont(AppTheme.FONT_SUBTITLE);
            chip.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
            riga.add(chip);
        }

        return riga;
    }

    private RoundedCardPanel creaSezioneContenuto(final RicettaCompleta dettaglio) {
        final RoundedCardPanel corpo = new RoundedCardPanel(null);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        final JLabel titoloIngredienti = new JLabel("Ingredienti");
        titoloIngredienti.setFont(AppTheme.FONT_BUTTON.deriveFont(15f));
        titoloIngredienti.setForeground(AppTheme.TEXT);
        titoloIngredienti.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(titoloIngredienti);
        corpo.add(Box.createVerticalStrut(8));

        for (final IngredienteRicetta ingrediente : dettaglio.getIngredienti()) {
            final JLabel riga = new JLabel("•  " + ingrediente.getNome()
                    + "  —  " + ingrediente.getQuantita() + " g");
            riga.setFont(AppTheme.FONT_BODY);
            riga.setForeground(AppTheme.TEXT);
            riga.setAlignmentX(Component.LEFT_ALIGNMENT);
            riga.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            corpo.add(riga);
        }

        corpo.add(Box.createVerticalStrut(24));

        final JLabel titoloPreparazione = new JLabel("Preparazione");
        titoloPreparazione.setFont(AppTheme.FONT_BUTTON.deriveFont(15f));
        titoloPreparazione.setForeground(AppTheme.TEXT);
        titoloPreparazione.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(titoloPreparazione);
        corpo.add(Box.createVerticalStrut(8));

        final JTextArea preparazione = new JTextArea(dettaglio.getRicetta().getPreparazione());
        preparazione.setLineWrap(true);
        preparazione.setWrapStyleWord(true);
        preparazione.setEditable(false);
        preparazione.setOpaque(false);
        preparazione.setFont(AppTheme.FONT_BODY);
        preparazione.setForeground(AppTheme.TEXT);
        preparazione.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(preparazione);

        return corpo;
    }

    private RoundedCardPanel creaCardAggiungiCarrello(final RicettaVista ricetta) {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(280, 160));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        final JLabel etichetta = new JLabel("Aggiungi al carrello");
        etichetta.setFont(AppTheme.FONT_BUTTON);
        etichetta.setForeground(AppTheme.TEXT);
        card.add(etichetta, BorderLayout.NORTH);

        final JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        final JPanel rigaQuantita = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rigaQuantita.setOpaque(false);
        final JSpinner quantitaSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        quantitaSpinner.setPreferredSize(new Dimension(60, 26));
        rigaQuantita.add(new JLabel("Quantità:"));
        rigaQuantita.add(quantitaSpinner);
        rigaQuantita.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JLabel statoAggiunta = new JLabel(" ");
        statoAggiunta.setFont(AppTheme.FONT_SUBTITLE);
        statoAggiunta.setAlignmentX(Component.LEFT_ALIGNMENT);

        corpo.add(rigaQuantita);
        corpo.add(Box.createVerticalStrut(6));
        corpo.add(statoAggiunta);
        card.add(corpo, BorderLayout.CENTER);

        final JButton aggiungiButton = new JButton("Aggiungi");
        aggiungiButton.setFont(AppTheme.FONT_BUTTON);
        aggiungiButton.setBackground(AppTheme.ACCENT);
        aggiungiButton.setForeground(Color.WHITE);
        aggiungiButton.setFocusPainted(false);
        aggiungiButton.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        aggiungiButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aggiungiButton.addActionListener(e -> {
            final int quantita = (int) quantitaSpinner.getValue();
            carrello.aggiungi(ricetta, quantita);
            statoAggiunta.setForeground(AppTheme.SUCCESS);
            statoAggiunta.setText("Aggiunta al carrello!");
        });
        card.add(aggiungiButton, BorderLayout.SOUTH);

        return card;
    }

    private RoundedCardPanel creaCardRecensione(final long codiceRicetta) {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(320, 160));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        final JLabel etichetta = new JLabel("Lascia una recensione");
        etichetta.setFont(AppTheme.FONT_BUTTON);
        etichetta.setForeground(AppTheme.TEXT);
        card.add(etichetta, BorderLayout.NORTH);

        final JPanel campi = new JPanel();
        campi.setOpaque(false);
        campi.setLayout(new BoxLayout(campi, BoxLayout.Y_AXIS));

        final JPanel rigaVoto = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rigaVoto.setOpaque(false);
        final JSpinner votoSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        votoSpinner.setPreferredSize(new Dimension(60, 26));
        rigaVoto.add(new JLabel("Voto:"));
        rigaVoto.add(votoSpinner);
        rigaVoto.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JTextField commentoField = new JTextField();
        commentoField.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        final JLabel statoRecensione = new JLabel(" ");
        statoRecensione.setFont(AppTheme.FONT_SUBTITLE);
        statoRecensione.setAlignmentX(Component.LEFT_ALIGNMENT);

        campi.add(rigaVoto);
        campi.add(Box.createVerticalStrut(6));
        campi.add(commentoField);
        campi.add(Box.createVerticalStrut(6));
        campi.add(statoRecensione);
        card.add(campi, BorderLayout.CENTER);

        final JButton inviaButton = new JButton("Invia recensione");
        inviaButton.setFont(AppTheme.FONT_BUTTON);
        inviaButton.setBackground(AppTheme.PRIMARY);
        inviaButton.setForeground(Color.WHITE);
        inviaButton.setFocusPainted(false);
        inviaButton.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        inviaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inviaButton.addActionListener(e -> {
            final int voto = (int) votoSpinner.getValue();
            final String commento = commentoField.getText().trim();

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ricettaController.pubblicaRecensione(
                            utenteCorrente.getCodiceUtente(), codiceRicetta, voto,
                            commento.isEmpty() ? null : commento);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        statoRecensione.setForeground(AppTheme.SUCCESS);
                        statoRecensione.setText("Recensione pubblicata!");
                        commentoField.setText("");
                    } catch (final Exception ex) {
                        statoRecensione.setForeground(AppTheme.ERROR);
                        statoRecensione.setText("Hai già recensito questa ricetta.");
                    }
                }
            }.execute();
        });
        card.add(inviaButton, BorderLayout.SOUTH);

        return card;
    }
}