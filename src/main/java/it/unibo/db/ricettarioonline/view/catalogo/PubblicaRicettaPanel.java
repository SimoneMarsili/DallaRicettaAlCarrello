package it.unibo.db.ricettarioonline.view.catalogo;

import it.unibo.db.ricettarioonline.controller.CatalogoController;
import it.unibo.db.ricettarioonline.controller.RicettaController;
import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.view.components.RoundedCardPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// U6 - Form di pubblicazione ricetta: dati base + selezione dinamica di
// ingredienti (con quantità) e categorie.
public class PubblicaRicettaPanel extends JPanel {

    private final CatalogoController catalogoController = new CatalogoController();
    private final RicettaController ricettaController = new RicettaController();
    private final Utente utenteCorrente;

    private final JTextField nomeField = new JTextField();
    private final JSpinner tempoSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 600, 5));
    private final JTextArea preparazioneArea = new JTextArea(6, 20);

    private final JComboBox<Ingrediente> comboIngredienti = new JComboBox<>();
    private final JSpinner quantitaSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 5000, 10));
    private final Map<Long, BigDecimal> ingredientiSelezionati = new LinkedHashMap<>();
    private final Map<Long, String> nomiIngredienti = new LinkedHashMap<>();
    private final JPanel listaIngredientiPanel = new JPanel();

    private final DefaultListModel<Categoria> modelloCategorie = new DefaultListModel<>();
    private final JList<Categoria> listaCategorie = new JList<>(modelloCategorie);

    private final JLabel statusLabel = new JLabel(" ");

    public PubblicaRicettaPanel(final Utente utenteCorrente) {
        super(new BorderLayout());
        this.utenteCorrente = utenteCorrente;
        setOpaque(false);

        final JScrollPane scroll = new JScrollPane(creaForm());
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        caricaDatiIniziali();
    }

    private JPanel creaForm() {
        final JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        final JLabel titolo = new JLabel("Pubblica una nuova ricetta");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.TEXT);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(titolo);
        form.add(Box.createVerticalStrut(20));

        form.add(creaSezioneDatiBase());
        form.add(Box.createVerticalStrut(16));
        form.add(creaSezioneIngredienti());
        form.add(Box.createVerticalStrut(16));
        form.add(creaSezioneCategorie());
        form.add(Box.createVerticalStrut(20));
        form.add(creaBottonePubblica());

        return form;
    }

    private RoundedCardPanel creaSezioneDatiBase() {
        final RoundedCardPanel card = new RoundedCardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        card.add(etichetta("Nome ricetta"));
        nomeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nomeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        card.add(nomeField);
        card.add(Box.createVerticalStrut(12));

        card.add(etichetta("Tempo richiesto (minuti)"));
        tempoSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        tempoSpinner.setMaximumSize(new Dimension(100, 28));
        card.add(tempoSpinner);
        card.add(Box.createVerticalStrut(12));

        card.add(etichetta("Preparazione"));
        preparazioneArea.setLineWrap(true);
        preparazioneArea.setWrapStyleWord(true);
        preparazioneArea.setFont(AppTheme.FONT_BODY);
        final JScrollPane areaScroll = new JScrollPane(preparazioneArea);
        areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.add(areaScroll);

        return card;
    }

    private RoundedCardPanel creaSezioneIngredienti() {
        final RoundedCardPanel card = new RoundedCardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(etichetta("Ingredienti"));

        final JPanel rigaAggiunta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        rigaAggiunta.setOpaque(false);
        comboIngredienti.setPreferredSize(new Dimension(200, 28));
        quantitaSpinner.setPreferredSize(new Dimension(80, 28));

        final JButton aggiungiButton = new JButton("Aggiungi");
        aggiungiButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aggiungiButton.addActionListener(e -> aggiungiIngredienteSelezionato());

        rigaAggiunta.add(comboIngredienti);
        rigaAggiunta.add(new JLabel("Quantità (g):"));
        rigaAggiunta.add(quantitaSpinner);
        rigaAggiunta.add(aggiungiButton);
        rigaAggiunta.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(rigaAggiunta);

        listaIngredientiPanel.setOpaque(false);
        listaIngredientiPanel.setLayout(new BoxLayout(listaIngredientiPanel, BoxLayout.Y_AXIS));
        listaIngredientiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(listaIngredientiPanel);

        return card;
    }

    private void aggiungiIngredienteSelezionato() {
        final Ingrediente ingrediente = (Ingrediente) comboIngredienti.getSelectedItem();
        if (ingrediente == null) {
            return;
        }
        final BigDecimal quantita = BigDecimal.valueOf((Integer) quantitaSpinner.getValue());

        ingredientiSelezionati.put(ingrediente.getCodiceIngrediente(), quantita);
        nomiIngredienti.put(ingrediente.getCodiceIngrediente(), ingrediente.getNome());
        aggiornaListaIngredienti();
    }

    private void aggiornaListaIngredienti() {
        listaIngredientiPanel.removeAll();

        for (final Map.Entry<Long, BigDecimal> voce : ingredientiSelezionati.entrySet()) {
            final JPanel riga = new JPanel(new BorderLayout());
            riga.setOpaque(false);
            riga.setAlignmentX(Component.LEFT_ALIGNMENT);
            riga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

            final JLabel testo = new JLabel("•  " + nomiIngredienti.get(voce.getKey())
                    + "  —  " + voce.getValue() + " g");
            testo.setFont(AppTheme.FONT_BODY);

            final JButton rimuoviButton = new JButton("Rimuovi");
            rimuoviButton.setFont(AppTheme.FONT_SUBTITLE);
            rimuoviButton.setForeground(AppTheme.ERROR);
            rimuoviButton.setBorderPainted(false);
            rimuoviButton.setContentAreaFilled(false);
            rimuoviButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rimuoviButton.addActionListener(e -> {
                ingredientiSelezionati.remove(voce.getKey());
                nomiIngredienti.remove(voce.getKey());
                aggiornaListaIngredienti();
            });

            riga.add(testo, BorderLayout.WEST);
            riga.add(rimuoviButton, BorderLayout.EAST);
            listaIngredientiPanel.add(riga);
        }

        listaIngredientiPanel.revalidate();
        listaIngredientiPanel.repaint();
    }

    private RoundedCardPanel creaSezioneCategorie() {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        card.add(etichetta("Categorie (tieni premuto Ctrl per selezionarne più di una)"), BorderLayout.NORTH);

        listaCategorie.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaCategorie.setFont(AppTheme.FONT_BODY);
        final JScrollPane scroll = new JScrollPane(listaCategorie);
        scroll.setPreferredSize(new Dimension(300, 100));
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel creaBottonePubblica() {
        final JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JButton pubblicaButton = new JButton("Pubblica ricetta");
        pubblicaButton.setFont(AppTheme.FONT_BUTTON);
        pubblicaButton.setBackground(AppTheme.PRIMARY);
        pubblicaButton.setForeground(Color.WHITE);
        pubblicaButton.setFocusPainted(false);
        pubblicaButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        pubblicaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pubblicaButton.addActionListener(e -> eseguiPubblicazione(pubblicaButton));

        final JPanel colonna = new JPanel();
        colonna.setOpaque(false);
        colonna.setLayout(new BoxLayout(colonna, BoxLayout.Y_AXIS));
        colonna.add(pubblicaButton);
        colonna.add(Box.createVerticalStrut(6));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colonna.add(statusLabel);

        wrapper.add(colonna);
        return wrapper;
    }

    private void eseguiPubblicazione(final JButton pubblicaButton) {
        final String nome = nomeField.getText().trim();
        final String preparazione = preparazioneArea.getText().trim();
        final int tempo = (int) tempoSpinner.getValue();
        final List<Categoria> categorieSelezionate = listaCategorie.getSelectedValuesList();

        if (nome.isEmpty() || preparazione.isEmpty() || ingredientiSelezionati.isEmpty()
                || categorieSelezionate.isEmpty()) {
            statusLabel.setForeground(AppTheme.ERROR);
            statusLabel.setText("Compila nome, preparazione, almeno un ingrediente e una categoria.");
            return;
        }

        pubblicaButton.setEnabled(false);
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Pubblicazione in corso...");

        final List<Long> codiciCategoria = categorieSelezionate.stream()
                .map(Categoria::getCodiceCategoria).toList();

        new SwingWorker<Long, Void>() {
            @Override
            protected Long doInBackground() throws Exception {
                return ricettaController.pubblicaRicetta(nome, utenteCorrente.getCodiceUtente(),
                        preparazione, tempo, ingredientiSelezionati, codiciCategoria);
            }

            @Override
            protected void done() {
                pubblicaButton.setEnabled(true);
                try {
                    get();
                    statusLabel.setForeground(AppTheme.SUCCESS);
                    statusLabel.setText("Ricetta pubblicata con successo!");
                    resetForm();
                } catch (final Exception ex) {
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Errore nella pubblicazione. Riprova.");
                }
            }
        }.execute();
    }

    private void resetForm() {
        nomeField.setText("");
        preparazioneArea.setText("");
        tempoSpinner.setValue(30);
        ingredientiSelezionati.clear();
        nomiIngredienti.clear();
        aggiornaListaIngredienti();
        listaCategorie.clearSelection();
    }

    private void caricaDatiIniziali() {
        new SwingWorker<Void, Void>() {
            private List<Ingrediente> ingredienti;
            private List<Categoria> categorie;

            @Override
            protected Void doInBackground() throws Exception {
                ingredienti = catalogoController.elencaIngredienti();
                categorie = catalogoController.elencaCategorie();
                return null;
            }

            @Override
            protected void done() {
                for (final Ingrediente i : ingredienti) {
                    comboIngredienti.addItem(i);
                }
                for (final Categoria c : categorie) {
                    modelloCategorie.addElement(c);
                }
            }
        }.execute();
    }

    private JLabel etichetta(final String testo) {
        final JLabel label = new JLabel(testo);
        label.setFont(AppTheme.FONT_BUTTON);
        label.setForeground(AppTheme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}