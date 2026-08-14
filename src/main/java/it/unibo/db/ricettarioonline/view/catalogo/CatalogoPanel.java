package it.unibo.db.ricettarioonline.view.catalogo;

import it.unibo.db.ricettarioonline.controller.RicettaController;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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

// Catalogo ricette con filtro (U2, U3.1-U3.6) e drill-down verso il dettaglio
// (U11) con pubblicazione recensione (U5). Gestisce un CardLayout interno
// (lista <-> dettaglio) per non far dipendere ShellPanel dal drill-down.
public class CatalogoPanel extends JPanel {

    private static final String CARD_LISTA = "CARD_LISTA";
    private static final String CARD_DETTAGLIO = "CARD_DETTAGLIO";

    private final RicettaController ricettaController = new RicettaController();
    private final Utente utenteCorrente;

    private final Carrello carrello;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final JPanel risultatiPanel = new JPanel();
    private final JComboBox<String> tipoFiltro = new JComboBox<>(new String[] {
            "Tutte", "Nome", "Ingrediente", "Tempo massimo (minuti)", "Fascia di prezzo", "Autore", "Categoria"
    });
    private final JTextField campoFiltro1 = new JTextField();
    private final JTextField campoFiltro2 = new JTextField(); // usato solo per fascia prezzo / autore
    private final JLabel statusLabel = new JLabel(" ");

    public CatalogoPanel(final Utente utenteCorrente, final Carrello carrello) {
        super(new BorderLayout());
        this.utenteCorrente = utenteCorrente;
        this.carrello = carrello;
        setOpaque(false);

        cards.add(creaCardLista(), CARD_LISTA);
        add(cards, BorderLayout.CENTER);

        caricaRicette(() -> ricettaController.elencaRicette());
    }

    private JPanel creaCardLista() {
        final JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        panel.add(creaPannelloFiltri(), BorderLayout.NORTH);

        risultatiPanel.setLayout(new BoxLayout(risultatiPanel, BoxLayout.Y_AXIS));
        risultatiPanel.setOpaque(false);

        final JScrollPane scroll = new JScrollPane(risultatiPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel creaPannelloFiltri() {
        final JPanel filtri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filtri.setOpaque(false);

        campoFiltro1.setPreferredSize(new Dimension(160, 28));
        campoFiltro2.setPreferredSize(new Dimension(80, 28));
        campoFiltro2.setVisible(false);

        tipoFiltro.addActionListener(e -> {
            final String scelta = (String) tipoFiltro.getSelectedItem();
            campoFiltro2.setVisible("Fascia di prezzo".equals(scelta) || "Autore".equals(scelta));
        });

        final JButton cercaButton = new JButton("Cerca");
        cercaButton.addActionListener(e -> eseguiRicerca());

        filtri.add(new JLabel("Filtra per:"));
        filtri.add(tipoFiltro);
        filtri.add(campoFiltro1);
        filtri.add(new JLabel("e"));
        filtri.add(campoFiltro2);
        filtri.add(cercaButton);
        filtri.add(statusLabel);

        return filtri;
    }

    private void eseguiRicerca() {
        final String scelta = (String) tipoFiltro.getSelectedItem();
        final String valore1 = campoFiltro1.getText().trim();
        final String valore2 = campoFiltro2.getText().trim();

        switch (scelta) {
            case "Nome" -> caricaRicette(() -> ricettaController.cercaPerNome(valore1));
            case "Ingrediente" -> caricaRicette(() -> ricettaController.cercaPerIngrediente(valore1));
            case "Tempo massimo (minuti)" -> caricaRicette(() ->
                    ricettaController.cercaPerTempoMax(Integer.parseInt(valore1)));
            case "Fascia di prezzo" -> caricaRicette(() -> ricettaController.cercaPerPrezzo(
                    new java.math.BigDecimal(valore1), new java.math.BigDecimal(valore2)));
            case "Autore" -> caricaRicette(() -> ricettaController.cercaPerAutore(valore1, valore2));
            case "Categoria" -> caricaRicette(() -> ricettaController.cercaPerCategoria(valore1));
            default -> caricaRicette(() -> ricettaController.elencaRicette());
        }
    }

    @FunctionalInterface
    private interface RicercaRicette {
        List<RicettaVista> esegui() throws Exception;
    }

    // U2/U3.x - Ogni ricerca gira in un SwingWorker per non bloccare la UI.
    private void caricaRicette(final RicercaRicette ricerca) {
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Caricamento...");

        new SwingWorker<List<RicettaVista>, Void>() {
            @Override
            protected List<RicettaVista> doInBackground() throws Exception {
                return ricerca.esegui();
            }

            @Override
            protected void done() {
                try {
                    mostraRisultati(get());
                    statusLabel.setText(" ");
                } catch (final Exception ex) {
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Errore nella ricerca.");
                }
            }
        }.execute();
    }

    private void mostraRisultati(final List<RicettaVista> ricette) {
        risultatiPanel.removeAll();

        if (ricette.isEmpty()) {
            final JLabel vuoto = new JLabel("Nessuna ricetta trovata.");
            vuoto.setForeground(AppTheme.TEXT_MUTED);
            vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
            risultatiPanel.add(vuoto);
        }

        for (final RicettaVista ricetta : ricette) {
            risultatiPanel.add(creaRigaRicetta(ricetta));
            risultatiPanel.add(Box.createVerticalStrut(10));
        }

        risultatiPanel.revalidate();
        risultatiPanel.repaint();
    }

    private RoundedCardPanel creaRigaRicetta(final RicettaVista ricetta) {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(12, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final JLabel nome = new JLabel(ricetta.getNomeRicetta());
        nome.setFont(AppTheme.FONT_BUTTON);
        nome.setForeground(AppTheme.TEXT);

        final JLabel dettagli = new JLabel(String.format("di %s %s · %d min · %.2f €",
                ricetta.getNomeAutore(), ricetta.getCognomeAutore(),
                ricetta.getTempoRichiesto(), ricetta.getPrezzoRicetta()));
        dettagli.setFont(AppTheme.FONT_SUBTITLE);
        dettagli.setForeground(AppTheme.TEXT_MUTED);

        final JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        testi.add(nome);
        testi.add(dettagli);

        card.add(testi, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                apriDettaglio(ricetta.getCodiceRicetta());
            }
        });

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
                                () -> cardLayout.show(cards, CARD_LISTA));
                        cards.add(panel, CARD_DETTAGLIO);
                        cardLayout.show(cards, CARD_DETTAGLIO);
                    }
                } catch (final Exception ex) {
                    JOptionPane.showMessageDialog(CatalogoPanel.this,
                            "Errore nel caricamento della ricetta.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}