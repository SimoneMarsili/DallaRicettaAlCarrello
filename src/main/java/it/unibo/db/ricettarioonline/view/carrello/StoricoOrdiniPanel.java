package it.unibo.db.ricettarioonline.view.carrello;

import it.unibo.db.ricettarioonline.controller.OrdineController;
import it.unibo.db.ricettarioonline.model.RigaStoricoOrdine;
import it.unibo.db.ricettarioonline.model.Utente;
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
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// U10 - Storico ordini dell'utente: la query restituisce una riga per ogni
// riga di dettaglio, qui la raggruppiamo per CodiceOrdine per mostrarla come
// un elenco di ordini, ciascuno con le sue righe.
public class StoricoOrdiniPanel extends JPanel {

    private final OrdineController ordineController = new OrdineController();
    private final Utente utenteCorrente;

    private final JPanel listaOrdiniPanel = new JPanel();
    private final JLabel statusLabel = new JLabel(" ");

    public StoricoOrdiniPanel(final Utente utenteCorrente) {
        super(new BorderLayout(0, 16));
        this.utenteCorrente = utenteCorrente;
        setOpaque(false);

        add(creaIntestazione(), BorderLayout.NORTH);

        listaOrdiniPanel.setLayout(new BoxLayout(listaOrdiniPanel, BoxLayout.Y_AXIS));
        listaOrdiniPanel.setOpaque(false);
        final JScrollPane scroll = new JScrollPane(listaOrdiniPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        caricaStorico();
    }

    private JPanel creaIntestazione() {
        final JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        final JLabel titolo = new JLabel("Storico ordini");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.TEXT);
        header.add(titolo, BorderLayout.WEST);

        final JPanel destra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        destra.setOpaque(false);
        final JButton aggiornaButton = new JButton("Aggiorna");
        aggiornaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aggiornaButton.addActionListener(e -> caricaStorico());
        destra.add(statusLabel);
        destra.add(aggiornaButton);
        header.add(destra, BorderLayout.EAST);

        return header;
    }

    // Chiamato anche dal pulsante "Aggiorna": lo storico non si aggiorna da
    // solo dopo un nuovo ordine (a differenza del Carrello, non è collegato
    // a un listener), quindi l'utente deve poterlo ricaricare manualmente.
    private void caricaStorico() {
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Caricamento...");

        new SwingWorker<List<RigaStoricoOrdine>, Void>() {
            @Override
            protected List<RigaStoricoOrdine> doInBackground() throws Exception {
                return ordineController.storicoOrdini(utenteCorrente.getCodiceUtente());
            }

            @Override
            protected void done() {
                try {
                    mostraStorico(get());
                    statusLabel.setText(" ");
                } catch (final Exception ex) {
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Errore nel caricamento.");
                }
            }
        }.execute();
    }

    private void mostraStorico(final List<RigaStoricoOrdine> righe) {
        listaOrdiniPanel.removeAll();

        if (righe.isEmpty()) {
            final JLabel vuoto = new JLabel("Non hai ancora effettuato ordini.");
            vuoto.setForeground(AppTheme.TEXT_MUTED);
            vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaOrdiniPanel.add(vuoto);
        }

        // Raggruppa per CodiceOrdine, mantenendo l'ordine di arrivo (già
        // ordinato per Data DESC, CodiceOrdine dalla query di U10).
        final Map<Long, List<RigaStoricoOrdine>> perOrdine = new LinkedHashMap<>();
        for (final RigaStoricoOrdine riga : righe) {
            perOrdine.computeIfAbsent(riga.getCodiceOrdine(), k -> new java.util.ArrayList<>()).add(riga);
        }

        for (final Map.Entry<Long, List<RigaStoricoOrdine>> ordine : perOrdine.entrySet()) {
            listaOrdiniPanel.add(creaCardOrdine(ordine.getKey(), ordine.getValue()));
            listaOrdiniPanel.add(Box.createVerticalStrut(14));
        }

        listaOrdiniPanel.revalidate();
        listaOrdiniPanel.repaint();
    }

    private RoundedCardPanel creaCardOrdine(final long codiceOrdine, final List<RigaStoricoOrdine> righeOrdine) {
        final RoundedCardPanel card = new RoundedCardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        final JPanel intestazioneOrdine = new JPanel(new BorderLayout());
        intestazioneOrdine.setOpaque(false);
        intestazioneOrdine.setAlignmentX(Component.LEFT_ALIGNMENT);
        intestazioneOrdine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        final JLabel titoloOrdine = new JLabel("Ordine del " + righeOrdine.get(0).getData());
        titoloOrdine.setFont(AppTheme.FONT_BUTTON);
        titoloOrdine.setForeground(AppTheme.TEXT);
        intestazioneOrdine.add(titoloOrdine, BorderLayout.WEST);

        final JLabel riferimentoLabel = new JLabel("Rif. #" + codiceOrdine);
        riferimentoLabel.setFont(AppTheme.FONT_SUBTITLE);
        riferimentoLabel.setForeground(AppTheme.TEXT_MUTED);
        intestazioneOrdine.add(riferimentoLabel, BorderLayout.EAST);

        card.add(intestazioneOrdine);
        card.add(Box.createVerticalStrut(8));

        BigDecimal totaleOrdine = BigDecimal.ZERO;
        for (final RigaStoricoOrdine riga : righeOrdine) {
            final JLabel rigaLabel = new JLabel(String.format(
                    "•  %d × %s  —  %.2f € cad.%s  =  %.2f €",
                    riga.getQuantita(), riga.getNomeRicetta(), riga.getPrezzoUnitario(),
                    riga.getScontoApplicato().signum() > 0
                            ? String.format(" (sconto %.0f%%)", riga.getScontoApplicato())
                            : "",
                    riga.getTotaleRiga()));
            rigaLabel.setFont(AppTheme.FONT_BODY);
            rigaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rigaLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            card.add(rigaLabel);

            totaleOrdine = totaleOrdine.add(riga.getTotaleRiga());
        }

        card.add(Box.createVerticalStrut(8));
        final JLabel totaleLabel = new JLabel(String.format("Totale ordine: %.2f €", totaleOrdine));
        totaleLabel.setFont(AppTheme.FONT_BUTTON);
        totaleLabel.setForeground(AppTheme.PRIMARY);
        totaleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totaleLabel);

        return card;
    }
}