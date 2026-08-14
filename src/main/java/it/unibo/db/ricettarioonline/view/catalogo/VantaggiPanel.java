package it.unibo.db.ricettarioonline.view.catalogo;

import it.unibo.db.ricettarioonline.controller.CatalogoController;
import it.unibo.db.ricettarioonline.model.VantaggioAttivo;
import it.unibo.db.ricettarioonline.view.components.RoundedCardPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

// U7 - Vantaggi/promozioni attualmente attive. Sola lettura, con pulsante
// "Aggiorna" (stesso pattern già usato in Classifiche/Storico ordini).
public class VantaggiPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CatalogoController catalogoController = new CatalogoController();

    private final JPanel elencoPanel = new JPanel();
    private final JLabel statusLabel = new JLabel(" ");

    public VantaggiPanel() {
        super(new BorderLayout(0, 16));
        setOpaque(false);

        add(creaIntestazione(), BorderLayout.NORTH);

        elencoPanel.setLayout(new BoxLayout(elencoPanel, BoxLayout.Y_AXIS));
        elencoPanel.setOpaque(false);
        final JScrollPane scroll = new JScrollPane(elencoPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        caricaVantaggi();
    }

    private JPanel creaIntestazione() {
        final JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        final JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        final JLabel titolo = new JLabel("Vantaggi attivi");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.TEXT);
        final JLabel sottotitolo = new JLabel("Sconti attualmente validi in base a categoria e numero di ingredienti");
        sottotitolo.setFont(AppTheme.FONT_SUBTITLE);
        sottotitolo.setForeground(AppTheme.TEXT_MUTED);
        testi.add(titolo);
        testi.add(sottotitolo);
        header.add(testi, BorderLayout.WEST);

        final JPanel destra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        destra.setOpaque(false);
        final JButton aggiornaButton = new JButton("Aggiorna");
        aggiornaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aggiornaButton.addActionListener(e -> caricaVantaggi());
        destra.add(statusLabel);
        destra.add(aggiornaButton);
        header.add(destra, BorderLayout.EAST);

        return header;
    }

    private void caricaVantaggi() {
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Caricamento...");

        new SwingWorker<List<VantaggioAttivo>, Void>() {
            @Override
            protected List<VantaggioAttivo> doInBackground() throws Exception {
                return catalogoController.vantaggiAttivi();
            }

            @Override
            protected void done() {
                elencoPanel.removeAll();
                try {
                    final List<VantaggioAttivo> vantaggi = get();
                    statusLabel.setText(" ");

                    if (vantaggi.isEmpty()) {
                        final JLabel vuoto = new JLabel("Nessuno sconto attivo al momento.");
                        vuoto.setForeground(AppTheme.TEXT_MUTED);
                        vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
                        elencoPanel.add(vuoto);
                    } else {
                        for (final VantaggioAttivo vantaggio : vantaggi) {
                            elencoPanel.add(creaCardVantaggio(vantaggio));
                            elencoPanel.add(Box.createVerticalStrut(10));
                        }
                    }
                } catch (final Exception ex) {
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Errore nel caricamento.");
                }
                elencoPanel.revalidate();
                elencoPanel.repaint();
            }
        }.execute();
    }

    private RoundedCardPanel creaCardVantaggio(final VantaggioAttivo vantaggio) {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(16, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        final JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));

        final JLabel nomePromo = new JLabel(vantaggio.getNomePromo() + " · categoria " + vantaggio.getNomeCategoria());
        nomePromo.setFont(AppTheme.FONT_BUTTON);
        nomePromo.setForeground(AppTheme.TEXT);

        final JLabel dettagli = new JLabel(String.format("Valido dal %d ingredienti al %d ingredienti · attivo dal %s al %s",
                vantaggio.getMinIngredienti(), vantaggio.getMaxIngredienti(),
                vantaggio.getDataInizio().format(FORMATO_DATA), vantaggio.getDataFine().format(FORMATO_DATA)));
        dettagli.setFont(AppTheme.FONT_SUBTITLE);
        dettagli.setForeground(AppTheme.TEXT_MUTED);

        testi.add(nomePromo);
        testi.add(dettagli);
        card.add(testi, BorderLayout.CENTER);

        final JLabel percentuale = new JLabel(String.format("-%.0f%%", vantaggio.getPercentualeSconto()));
        percentuale.setFont(AppTheme.FONT_TITLE);
        percentuale.setForeground(AppTheme.ACCENT);
        card.add(percentuale, BorderLayout.EAST);

        return card;
    }
}