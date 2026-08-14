package it.unibo.db.ricettarioonline.view.report;

import it.unibo.db.ricettarioonline.controller.ReportController;
import it.unibo.db.ricettarioonline.controller.RicettaController;
import it.unibo.db.ricettarioonline.model.RicettaCompleta;
import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.view.carrello.Carrello;
import it.unibo.db.ricettarioonline.view.catalogo.RicettaDettaglioPanel;
import it.unibo.db.ricettarioonline.view.components.RoundedCardPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

// U8.1-U8.4 - Quattro classifiche in altrettante schede. Le righe delle due
// classifiche legate a ricette (migliori ricette, ricette più ordinate) sono
// cliccabili e aprono lo stesso dettaglio completo del catalogo (RicettaDettaglioPanel).
public class ClassifichePanel extends JPanel {

    private static final int LIMIT = 10;

    private static final String CARD_TABS = "CARD_TABS";
    private static final String CARD_DETTAGLIO = "CARD_DETTAGLIO";

    private final ReportController reportController = new ReportController();
    private final RicettaController ricettaController = new RicettaController();
    private final Utente utenteCorrente;
    private final Carrello carrello;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public ClassifichePanel(final Utente utenteCorrente, final Carrello carrello) {
        super(new BorderLayout());
        this.utenteCorrente = utenteCorrente;
        this.carrello = carrello;
        setOpaque(false);

        cards.add(creaTabsPanel(), CARD_TABS);
        add(cards, BorderLayout.CENTER);
    }

    private JPanel creaTabsPanel() {
        final JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        final JLabel titolo = new JLabel("Classifiche");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.TEXT);
        panel.add(titolo, BorderLayout.NORTH);

        final JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BUTTON);

        // Cliccabili: passiamo un estrattore del CodiceRicetta.
        tabs.addTab("Migliori ricette", creaScheda(
                () -> reportController.migliriRicette(LIMIT),
                r -> String.format("%s — voto medio %.1f/10", r.getNomeRicetta(), r.getMediaRecensioni()),
                r -> r.getCodiceRicetta()));

        tabs.addTab("Ricette più ordinate", creaScheda(
                () -> reportController.ricettePiuOrdinate(LIMIT),
                r -> String.format("%s — %d unità ordinate", r.getNomeRicetta(), r.getQuantitaTotaleOrdinata()),
                r -> r.getCodiceRicetta()));

        // Non cliccabili: nessun estrattore di codice ricetta (null).
        tabs.addTab("Migliori utenti", creaScheda(
                () -> reportController.miglioriUtenti(LIMIT),
                u -> String.format("%s %s — rating medio %.1f (%d ricette)",
                        u.getNome(), u.getCognome(), u.getRatingUtente(), u.getNumeroRicette()),
                null));

        tabs.addTab("Categorie più ordinate", creaScheda(
                () -> reportController.categoriePiuOrdinate(LIMIT),
                c -> String.format("%s — %d unità ordinate", c.getNomeCategoria(), c.getQuantitaTotaleOrdinata()),
                null));

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    @FunctionalInterface
    private interface CaricaDati<T> {
        List<T> esegui() throws Exception;
    }

    // Metodo generico: se codiceExtractor non è null, ogni riga diventa
    // cliccabile e apre il dettaglio della ricetta corrispondente. Include un
    // pulsante "Aggiorna", perché i dati non si aggiornano da soli quando
    // l'utente pubblica/ordina/recensisce una ricetta da un'altra schermata.
    private <T> JPanel creaScheda(final CaricaDati<T> caricaDati, final Function<T, String> formattatore,
            final Function<T, Long> codiceExtractor) {

        final JPanel scheda = new JPanel(new BorderLayout(0, 8));
        scheda.setOpaque(false);
        scheda.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        final JPanel elenco = new JPanel();
        elenco.setLayout(new BoxLayout(elenco, BoxLayout.Y_AXIS));
        elenco.setOpaque(false);

        final JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.FONT_SUBTITLE);

        final JButton aggiornaButton = new JButton("Aggiorna");
        aggiornaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final JPanel intestazione = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        intestazione.setOpaque(false);
        intestazione.add(aggiornaButton);
        intestazione.add(statusLabel);
        scheda.add(intestazione, BorderLayout.NORTH);

        final JScrollPane scroll = new JScrollPane(elenco);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scheda.add(scroll, BorderLayout.CENTER);

        final Runnable carica = () -> {
            statusLabel.setForeground(AppTheme.TEXT_MUTED);
            statusLabel.setText("Caricamento...");

            new SwingWorker<List<T>, Void>() {
                @Override
                protected List<T> doInBackground() throws Exception {
                    return caricaDati.esegui();
                }

                @Override
                protected void done() {
                    elenco.removeAll();
                    try {
                        final List<T> risultati = get();
                        statusLabel.setText(" ");
                        if (risultati.isEmpty()) {
                            final JLabel vuoto = new JLabel("Nessun dato disponibile.");
                            vuoto.setForeground(AppTheme.TEXT_MUTED);
                            vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
                            elenco.add(vuoto);
                        } else {
                            int posizione = 1;
                            for (final T elemento : risultati) {
                                final Long codiceRicetta = codiceExtractor == null
                                        ? null : codiceExtractor.apply(elemento);
                                elenco.add(creaRiga(posizione, formattatore.apply(elemento), codiceRicetta));
                                elenco.add(Box.createVerticalStrut(8));
                                posizione++;
                            }
                        }
                    } catch (final Exception ex) {
                        statusLabel.setForeground(AppTheme.ERROR);
                        statusLabel.setText("Errore nel caricamento.");
                    }
                    elenco.revalidate();
                    elenco.repaint();
                }
            }.execute();
        };

        aggiornaButton.addActionListener(e -> carica.run());
        carica.run(); // primo caricamento, all'apertura della scheda

        return scheda;
    }

    private RoundedCardPanel creaRiga(final int posizione, final String testo, final Long codiceRicetta) {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(14, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        final JLabel numero = new JLabel("#" + posizione);
        numero.setFont(AppTheme.FONT_BUTTON);
        numero.setForeground(posizione <= 3 ? AppTheme.PRIMARY : AppTheme.TEXT_MUTED);
        numero.setPreferredSize(new Dimension(36, 20));
        card.add(numero, BorderLayout.WEST);

        final JLabel testoLabel = new JLabel(testo);
        testoLabel.setFont(AppTheme.FONT_BODY);
        testoLabel.setForeground(AppTheme.TEXT);
        card.add(testoLabel, BorderLayout.CENTER);

        if (codiceRicetta != null) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    apriDettaglio(codiceRicetta);
                }
            });
        }

        return card;
    }

    private void apriDettaglio(final long codiceRicetta) {
        new SwingWorker<Optional<RicettaCompleta>, Void>() {
            @Override
            protected Optional<RicettaCompleta> doInBackground() throws Exception {
                return ricettaController.getDettaglioRicetta(codiceRicetta);
            }

            @Override
            protected void done() {
                try {
                    final Optional<RicettaCompleta> dettaglio = get();
                    if (dettaglio.isPresent()) {
                        final RicettaDettaglioPanel panel = new RicettaDettaglioPanel(
                                utenteCorrente, carrello, dettaglio.get(),
                                () -> cardLayout.show(cards, CARD_TABS));
                        cards.add(panel, CARD_DETTAGLIO);
                        cardLayout.show(cards, CARD_DETTAGLIO);
                    }
                } catch (final Exception ex) {
                    JOptionPane.showMessageDialog(ClassifichePanel.this,
                            "Errore nel caricamento della ricetta.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}