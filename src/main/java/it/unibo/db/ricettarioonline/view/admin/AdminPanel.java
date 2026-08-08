package it.unibo.db.ricettarioonline.view.admin;

import it.unibo.db.ricettarioonline.controller.AmministrazioneController;
import it.unibo.db.ricettarioonline.controller.CatalogoController;
import it.unibo.db.ricettarioonline.model.Categoria;
import it.unibo.db.ricettarioonline.model.FatturatoGiornaliero;
import it.unibo.db.ricettarioonline.model.FatturatoRicetta;
import it.unibo.db.ricettarioonline.model.Ingrediente;
import it.unibo.db.ricettarioonline.model.Promozione;
import it.unibo.db.ricettarioonline.model.RecensioneNegativa;
import it.unibo.db.ricettarioonline.view.components.RoundedCardPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// A1-A6 + le tre viste amministrative (v_fatturato_giornaliero,
// v_fatturato_per_ricetta, v_recensioni_negative_recenti). Palette blu/indaco
// dedicata (AppTheme.ADMIN_*), volutamente diversa dall'arancio della parte
// utente, per segnalare chiaramente il cambio di contesto.
public class AdminPanel extends JPanel {

    private final AmministrazioneController amministrazioneController = new AmministrazioneController();
    private final CatalogoController catalogoController = new CatalogoController();

    public AdminPanel() {
        super(new BorderLayout(0, 16));
        setOpaque(true);
        setBackground(AppTheme.ADMIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        add(creaIntestazione(), BorderLayout.NORTH);

        final JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BUTTON);
        tabs.addTab("Ingredienti", creaTabIngredienti());
        tabs.addTab("Categorie", creaTabCategorie());
        tabs.addTab("Promozioni & Sconti", creaTabPromozioniSconti());
        tabs.addTab("Utenti", creaTabUtenti());
        tabs.addTab("Report", creaTabReport());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel creaIntestazione() {
        final JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(AppTheme.ADMIN_PRIMARY);
        banner.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        final JLabel titolo = new JLabel("Pannello amministratore");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(Color.WHITE);

        final JLabel sottotitolo = new JLabel("Gestione ingredienti, categorie, promozioni, sconti e utenti");
        sottotitolo.setFont(AppTheme.FONT_SUBTITLE);
        sottotitolo.setForeground(AppTheme.ADMIN_ACCENT);

        final JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        testi.add(titolo);
        testi.add(sottotitolo);

        banner.add(testi, BorderLayout.WEST);
        return banner;
    }

    // ============================== INGREDIENTI (A1 + A2) ==============================

    private JPanel creaTabIngredienti() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(creaCardNuovoIngrediente());
        panel.add(Box.createVerticalStrut(16));
        panel.add(creaCardAggiornaIngrediente());

        final JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        return wrapInPanel(scroll);
    }

    private RoundedCardPanel creaCardNuovoIngrediente() {
        final RoundedCardPanel card = creaCardBase("Aggiungi nuovo ingrediente");

        final JTextField nomeField = new JTextField();
        final JTextField prezzoField = new JTextField();
        final JCheckBox veganoCheck = new JCheckBox("Vegano");
        final JLabel statusLabel = new JLabel(" ");

        final JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga.setOpaque(false);
        nomeField.setPreferredSize(new Dimension(180, 28));
        prezzoField.setPreferredSize(new Dimension(90, 28));
        riga.add(new JLabel("Nome:"));
        riga.add(nomeField);
        riga.add(new JLabel("Prezzo (€/kg):"));
        riga.add(prezzoField);
        riga.add(veganoCheck);

        final JButton creaButton = creaBottoneAdmin("Crea ingrediente");
        creaButton.addActionListener(e -> {
            try {
                final String nome = nomeField.getText().trim();
                final BigDecimal prezzo = new BigDecimal(prezzoField.getText().trim());
                new SwingWorker<Long, Void>() {
                    @Override
                    protected Long doInBackground() throws Exception {
                        return amministrazioneController.inserisciIngrediente(nome, prezzo, veganoCheck.isSelected());
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            statusLabel.setForeground(AppTheme.SUCCESS);
                            statusLabel.setText("Ingrediente creato.");
                            nomeField.setText("");
                            prezzoField.setText("");
                            veganoCheck.setSelected(false);
                        } catch (final Exception ex) {
                            statusLabel.setForeground(AppTheme.ERROR);
                            statusLabel.setText("Errore: nome duplicato o dati non validi.");
                        }
                    }
                }.execute();
            } catch (final NumberFormatException ex) {
                statusLabel.setForeground(AppTheme.ERROR);
                statusLabel.setText("Il prezzo deve essere un numero valido.");
            }
        });

        card.add(riga);
        card.add(creaBottoneEStatoRow(creaButton, statusLabel));
        return card;
    }

    private RoundedCardPanel creaCardAggiornaIngrediente() {
        final RoundedCardPanel card = creaCardBase("Modifica ingrediente esistente");

        final JComboBox<Ingrediente> comboIngredienti = new JComboBox<>();
        final JTextField nomeField = new JTextField();
        final JTextField prezzoField = new JTextField();
        final JCheckBox veganoCheck = new JCheckBox("Vegano");
        final JLabel statusLabel = new JLabel(" ");

        comboIngredienti.addActionListener(e -> {
            final Ingrediente selezionato = (Ingrediente) comboIngredienti.getSelectedItem();
            if (selezionato != null) {
                nomeField.setText(selezionato.getNome());
                prezzoField.setText(selezionato.getPrezzo().toString());
                veganoCheck.setSelected(selezionato.isVegano());
            }
        });

        caricaIngredienti(comboIngredienti);

        final JPanel riga1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga1.setOpaque(false);
        comboIngredienti.setPreferredSize(new Dimension(200, 28));
        riga1.add(new JLabel("Seleziona:"));
        riga1.add(comboIngredienti);

        final JPanel riga2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga2.setOpaque(false);
        nomeField.setPreferredSize(new Dimension(180, 28));
        prezzoField.setPreferredSize(new Dimension(90, 28));
        riga2.add(new JLabel("Nome:"));
        riga2.add(nomeField);
        riga2.add(new JLabel("Prezzo (€/kg):"));
        riga2.add(prezzoField);
        riga2.add(veganoCheck);

        final JButton aggiornaButton = creaBottoneAdmin("Salva modifiche");
        aggiornaButton.addActionListener(e -> {
            final Ingrediente selezionato = (Ingrediente) comboIngredienti.getSelectedItem();
            if (selezionato == null) {
                return;
            }
            try {
                final String nome = nomeField.getText().trim();
                final BigDecimal prezzo = new BigDecimal(prezzoField.getText().trim());
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        amministrazioneController.aggiornaIngrediente(
                                selezionato.getCodiceIngrediente(), nome, prezzo, veganoCheck.isSelected());
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            statusLabel.setForeground(AppTheme.SUCCESS);
                            statusLabel.setText("Ingrediente aggiornato.");
                            comboIngredienti.removeAllItems();
                            caricaIngredienti(comboIngredienti);
                        } catch (final Exception ex) {
                            statusLabel.setForeground(AppTheme.ERROR);
                            statusLabel.setText("Errore nell'aggiornamento.");
                        }
                    }
                }.execute();
            } catch (final NumberFormatException ex) {
                statusLabel.setForeground(AppTheme.ERROR);
                statusLabel.setText("Il prezzo deve essere un numero valido.");
            }
        });

        card.add(riga1);
        card.add(riga2);
        card.add(creaBottoneEStatoRow(aggiornaButton, statusLabel));
        return card;
    }

    private void caricaIngredienti(final JComboBox<Ingrediente> combo) {
        new SwingWorker<List<Ingrediente>, Void>() {
            @Override
            protected List<Ingrediente> doInBackground() throws Exception {
                return catalogoController.elencaIngredienti();
            }

            @Override
            protected void done() {
                try {
                    for (final Ingrediente i : get()) {
                        combo.addItem(i);
                    }
                } catch (final Exception ignored) {
                    // lista vuota in caso di errore, non blocca il resto della UI
                }
            }
        }.execute();
    }

    // ============================== CATEGORIE (A3) ==============================

    private JPanel creaTabCategorie() {
        final RoundedCardPanel card = creaCardBase("Crea o aggiorna categoria");

        final JTextField nomeField = new JTextField();
        final JTextField descrizioneField = new JTextField();
        final JLabel statusLabel = new JLabel(" ");

        final JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga.setOpaque(false);
        nomeField.setPreferredSize(new Dimension(160, 28));
        descrizioneField.setPreferredSize(new Dimension(260, 28));
        riga.add(new JLabel("Nome:"));
        riga.add(nomeField);
        riga.add(new JLabel("Descrizione:"));
        riga.add(descrizioneField);

        final JButton salvaButton = creaBottoneAdmin("Salva categoria");
        salvaButton.addActionListener(e -> {
            final String nome = nomeField.getText().trim();
            final String descrizione = descrizioneField.getText().trim();
            new SwingWorker<Long, Void>() {
                @Override
                protected Long doInBackground() throws Exception {
                    return amministrazioneController.inserisciOAggiornaCategoria(nome, descrizione);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        statusLabel.setForeground(AppTheme.SUCCESS);
                        statusLabel.setText("Categoria salvata (creata o aggiornata se il nome esisteva già).");
                        nomeField.setText("");
                        descrizioneField.setText("");
                    } catch (final Exception ex) {
                        statusLabel.setForeground(AppTheme.ERROR);
                        statusLabel.setText("Errore nel salvataggio della categoria.");
                    }
                }
            }.execute();
        });

        card.add(riga);
        card.add(creaBottoneEStatoRow(salvaButton, statusLabel));

        return wrapInPanel(card);
    }

    // ============================== PROMOZIONI & SCONTI (A4 + A5) ==============================

    private JPanel creaTabPromozioniSconti() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(creaCardNuovaPromozione());
        panel.add(Box.createVerticalStrut(16));
        panel.add(creaCardNuovoSconto());

        final JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        return wrapInPanel(scroll);
    }

    private RoundedCardPanel creaCardNuovaPromozione() {
        final RoundedCardPanel card = creaCardBase("Lancia promozione");

        final JTextField nomeField = new JTextField();
        final JTextField dataInizioField = new JTextField("aaaa-mm-gg");
        final JTextField dataFineField = new JTextField("aaaa-mm-gg");
        final JLabel statusLabel = new JLabel(" ");

        final JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga.setOpaque(false);
        nomeField.setPreferredSize(new Dimension(160, 28));
        dataInizioField.setPreferredSize(new Dimension(100, 28));
        dataFineField.setPreferredSize(new Dimension(100, 28));
        riga.add(new JLabel("Nome:"));
        riga.add(nomeField);
        riga.add(new JLabel("Dal:"));
        riga.add(dataInizioField);
        riga.add(new JLabel("Al:"));
        riga.add(dataFineField);

        final JButton creaButton = creaBottoneAdmin("Crea promozione");
        creaButton.addActionListener(e -> {
            try {
                final String nome = nomeField.getText().trim();
                final LocalDate dataInizio = LocalDate.parse(dataInizioField.getText().trim());
                final LocalDate dataFine = LocalDate.parse(dataFineField.getText().trim());

                new SwingWorker<Long, Void>() {
                    @Override
                    protected Long doInBackground() throws Exception {
                        return amministrazioneController.lanciaPromozione(nome, dataInizio, dataFine);
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            statusLabel.setForeground(AppTheme.SUCCESS);
                            statusLabel.setText("Promozione creata.");
                            nomeField.setText("");
                        } catch (final Exception ex) {
                            statusLabel.setForeground(AppTheme.ERROR);
                            statusLabel.setText("Errore: verifica che la data di fine sia successiva a quella di inizio.");
                        }
                    }
                }.execute();
            } catch (final Exception ex) {
                statusLabel.setForeground(AppTheme.ERROR);
                statusLabel.setText("Formato data non valido. Usa aaaa-mm-gg (es. 2027-01-31).");
            }
        });

        card.add(riga);
        card.add(creaBottoneEStatoRow(creaButton, statusLabel));
        return card;
    }

    private RoundedCardPanel creaCardNuovoSconto() {
        final RoundedCardPanel card = creaCardBase("Pubblica sconto");

        final JComboBox<Categoria> comboCategorie = new JComboBox<>();
        final JComboBox<Promozione> comboPromozioni = new JComboBox<>();
        final JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));
        final JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 50, 1));
        final JTextField percentualeField = new JTextField();
        final JLabel statusLabel = new JLabel(" ");

        caricaCategorie(comboCategorie);
        caricaPromozioni(comboPromozioni);

        final JPanel riga1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga1.setOpaque(false);
        comboCategorie.setPreferredSize(new Dimension(180, 28));
        comboPromozioni.setPreferredSize(new Dimension(220, 28));
        riga1.add(new JLabel("Categoria:"));
        riga1.add(comboCategorie);
        riga1.add(new JLabel("Promozione:"));
        riga1.add(comboPromozioni);

        final JPanel riga2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        riga2.setOpaque(false);
        percentualeField.setPreferredSize(new Dimension(70, 28));
        riga2.add(new JLabel("Min ingredienti:"));
        riga2.add(minSpinner);
        riga2.add(new JLabel("Max ingredienti:"));
        riga2.add(maxSpinner);
        riga2.add(new JLabel("Sconto (%):"));
        riga2.add(percentualeField);

        final JButton pubblicaButton = creaBottoneAdmin("Pubblica sconto");
        pubblicaButton.addActionListener(e -> {
            final Categoria categoria = (Categoria) comboCategorie.getSelectedItem();
            final Promozione promozione = (Promozione) comboPromozioni.getSelectedItem();
            if (categoria == null || promozione == null) {
                statusLabel.setForeground(AppTheme.ERROR);
                statusLabel.setText("Seleziona una categoria e una promozione.");
                return;
            }
            try {
                final BigDecimal percentuale = new BigDecimal(percentualeField.getText().trim());
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        amministrazioneController.pubblicaSconto(categoria.getCodiceCategoria(),
                                promozione.getCodicePromo(), (int) minSpinner.getValue(),
                                (int) maxSpinner.getValue(), percentuale);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            statusLabel.setForeground(AppTheme.SUCCESS);
                            statusLabel.setText("Sconto pubblicato.");
                        } catch (final Exception ex) {
                            statusLabel.setForeground(AppTheme.ERROR);
                            statusLabel.setText("Errore: intervallo ingredienti sovrapposto per la stessa categoria/promozione?");
                        }
                    }
                }.execute();
            } catch (final NumberFormatException ex) {
                statusLabel.setForeground(AppTheme.ERROR);
                statusLabel.setText("La percentuale deve essere un numero valido.");
            }
        });

        card.add(riga1);
        card.add(riga2);
        card.add(creaBottoneEStatoRow(pubblicaButton, statusLabel));
        return card;
    }

    private void caricaCategorie(final JComboBox<Categoria> combo) {
        new SwingWorker<List<Categoria>, Void>() {
            @Override
            protected List<Categoria> doInBackground() throws Exception {
                return catalogoController.elencaCategorie();
            }

            @Override
            protected void done() {
                try {
                    for (final Categoria c : get()) {
                        combo.addItem(c);
                    }
                } catch (final Exception ignored) {
                    // vedi nota in caricaIngredienti
                }
            }
        }.execute();
    }

    private void caricaPromozioni(final JComboBox<Promozione> combo) {
        new SwingWorker<List<Promozione>, Void>() {
            @Override
            protected List<Promozione> doInBackground() throws Exception {
                return amministrazioneController.elencaPromozioni();
            }

            @Override
            protected void done() {
                try {
                    for (final Promozione p : get()) {
                        combo.addItem(p);
                    }
                } catch (final Exception ignored) {
                    // vedi nota in caricaIngredienti
                }
            }
        }.execute();
    }

    // ============================== UTENTI (A6) ==============================

    private JPanel creaTabUtenti() {
        final RoundedCardPanel card = creaCardBase("Rimozione utenti incoerenti");

        final JLabel descrizione = new JLabel("<html><body style='width: 420px'>"
                + "Disattiva e rimuove le ricette degli utenti che hanno pubblicato almeno 3 ricette "
                + "classificate come \"Vegano\" pur contenendo ingredienti non vegani. "
                + "L'operazione è irreversibile.</body></html>");
        descrizione.setFont(AppTheme.FONT_BODY);
        descrizione.setForeground(AppTheme.TEXT);
        descrizione.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JLabel statusLabel = new JLabel(" ");

        final JButton eseguiButton = creaBottoneAdmin("Esegui rimozione");
        eseguiButton.addActionListener(e -> {
            final int conferma = JOptionPane.showConfirmDialog(this,
                    "Confermi l'esecuzione? L'operazione non è reversibile.",
                    "Conferma A6", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conferma != JOptionPane.YES_OPTION) {
                return;
            }

            eseguiButton.setEnabled(false);
            statusLabel.setForeground(AppTheme.TEXT_MUTED);
            statusLabel.setText("Esecuzione in corso...");

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    amministrazioneController.rimuoviUtentiIncoerenti();
                    return null;
                }

                @Override
                protected void done() {
                    eseguiButton.setEnabled(true);
                    try {
                        get();
                        statusLabel.setForeground(AppTheme.SUCCESS);
                        statusLabel.setText("Operazione completata.");
                    } catch (final Exception ex) {
                        statusLabel.setForeground(AppTheme.ERROR);
                        statusLabel.setText("Errore durante l'esecuzione.");
                    }
                }
            }.execute();
        });

        card.add(descrizione);
        card.add(Box.createVerticalStrut(12));
        card.add(creaBottoneEStatoRow(eseguiButton, statusLabel));

        return wrapInPanel(card);
    }

    // ============================== REPORT (viste) ==============================

    private JPanel creaTabReport() {
        final JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(AppTheme.FONT_SUBTITLE);

        subTabs.addTab("Fatturato giornaliero", creaTabellaFatturatoGiornaliero());
        subTabs.addTab("Fatturato per ricetta", creaTabellaFatturatoPerRicetta());
        subTabs.addTab("Recensioni negative recenti", creaTabellaRecensioniNegative());

        final JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        wrapper.add(subTabs, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel creaTabellaFatturatoGiornaliero() {
        final String[] colonne = { "Data", "N. ordini", "Quantità venduta", "Incasso (€)" };
        final DefaultTableModel modello = new DefaultTableModel(colonne, 0);
        final JTable tabella = creaTabellaAdmin(modello);

        return creaPannelloTabella(tabella, () -> {
            final List<FatturatoGiornaliero> righe = amministrazioneController.fatturatoGiornaliero();
            modello.setRowCount(0);
            for (final FatturatoGiornaliero r : righe) {
                modello.addRow(new Object[] { r.getData(), r.getNumeroOrdini(),
                        r.getQuantitaTotaleVenduta(), r.getIncassoTotale() });
            }
        });
    }

    private JPanel creaTabellaFatturatoPerRicetta() {
        final String[] colonne = { "Ricetta", "N. ordini", "Quantità venduta", "Incasso (€)" };
        final DefaultTableModel modello = new DefaultTableModel(colonne, 0);
        final JTable tabella = creaTabellaAdmin(modello);

        return creaPannelloTabella(tabella, () -> {
            final List<FatturatoRicetta> righe = amministrazioneController.fatturatoPerRicetta();
            modello.setRowCount(0);
            for (final FatturatoRicetta r : righe) {
                modello.addRow(new Object[] { r.getNomeRicetta(), r.getNumeroOrdini(),
                        r.getQuantitaTotaleVenduta(), r.getIncassoTotale() });
            }
        });
    }

    private JPanel creaTabellaRecensioniNegative() {
        final String[] colonne = { "Ricetta", "Utente", "Voto", "Commento", "Data" };
        final DefaultTableModel modello = new DefaultTableModel(colonne, 0);
        final JTable tabella = creaTabellaAdmin(modello);

        return creaPannelloTabella(tabella, () -> {
            final List<RecensioneNegativa> righe = amministrazioneController.recensioniNegativeRecenti();
            modello.setRowCount(0);
            for (final RecensioneNegativa r : righe) {
                modello.addRow(new Object[] { r.getNomeRicetta(), r.getNomeUtente() + " " + r.getCognomeUtente(),
                        r.getVoto(), r.getCommento(), r.getData() });
            }
        });
    }

    @FunctionalInterface
    private interface CaricaTabella {
        void esegui() throws Exception;
    }

    private JPanel creaPannelloTabella(final JTable tabella, final CaricaTabella caricamento) {
        final JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        final JLabel statusLabel = new JLabel(" ");
        final JButton aggiornaButton = creaBottoneAdmin("Aggiorna");

        final Runnable carica = () -> {
            statusLabel.setForeground(AppTheme.TEXT_MUTED);
            statusLabel.setText("Caricamento...");
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    caricamento.esegui();
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        statusLabel.setText(" ");
                    } catch (final Exception ex) {
                        statusLabel.setForeground(AppTheme.ERROR);
                        statusLabel.setText("Errore nel caricamento.");
                    }
                }
            }.execute();
        };

        aggiornaButton.addActionListener(e -> carica.run());

        final JPanel intestazione = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        intestazione.setOpaque(false);
        intestazione.add(aggiornaButton);
        intestazione.add(statusLabel);
        panel.add(intestazione, BorderLayout.NORTH);

        final JScrollPane scroll = new JScrollPane(tabella);
        panel.add(scroll, BorderLayout.CENTER);

        carica.run();
        return panel;
    }

    private JTable creaTabellaAdmin(final DefaultTableModel modello) {
        final JTable tabella = new JTable(modello) {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false; // sola lettura, sono report
            }
        };
        tabella.setFont(AppTheme.FONT_BODY);
        tabella.setRowHeight(26);
        tabella.getTableHeader().setFont(AppTheme.FONT_BUTTON);
        tabella.getTableHeader().setBackground(AppTheme.ADMIN_PRIMARY);
        tabella.getTableHeader().setForeground(Color.WHITE);
        tabella.setSelectionBackground(AppTheme.ADMIN_ACCENT);
        return tabella;
    }

    // ============================== HELPER CONDIVISI ==============================

    private RoundedCardPanel creaCardBase(final String titolo) {
        final RoundedCardPanel card = new RoundedCardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        final JLabel titoloLabel = new JLabel(titolo);
        titoloLabel.setFont(AppTheme.FONT_BUTTON.deriveFont(15f));
        titoloLabel.setForeground(AppTheme.ADMIN_PRIMARY);
        titoloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titoloLabel);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private JPanel creaBottoneEStatoRow(final JButton bottone, final JLabel statusLabel) {
        final JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        riga.setOpaque(false);
        riga.setAlignmentX(Component.LEFT_ALIGNMENT);
        riga.add(bottone);
        riga.add(statusLabel);
        return riga;
    }

    private JButton creaBottoneAdmin(final String testo) {
        final JButton button = new JButton(testo);
        button.setFont(AppTheme.FONT_BUTTON);
        button.setBackground(AppTheme.ADMIN_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel wrapInPanel(final JScrollPane scroll) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapInPanel(final RoundedCardPanel card) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(card, BorderLayout.NORTH);
        return panel;
    }
}