package it.unibo.db.ricettarioonline.view.carrello;

import it.unibo.db.ricettarioonline.controller.OrdineController;
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
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;

// U9 - Riepilogo del carrello e conferma ordine. Si aggiorna automaticamente
// ogni volta che il carrello cambia (tramite Carrello.Listener), quindi
// riflette gli articoli aggiunti dal catalogo senza bisogno di essere
// ricaricato manualmente.
public class CarrelloPanel extends JPanel {

    private final OrdineController ordineController = new OrdineController();
    private final Carrello carrello;
    private final Utente utenteCorrente;

    private final JPanel righePanel = new JPanel();
    private final JLabel totaleLabel = new JLabel();
    private final JTextField noteField = new JTextField();
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton confermaButton = new JButton("Conferma ordine");

    public CarrelloPanel(final Utente utenteCorrente, final Carrello carrello) {
        super(new BorderLayout(0, 16));
        this.utenteCorrente = utenteCorrente;
        this.carrello = carrello;
        setOpaque(false);

        final JLabel titolo = new JLabel("Il tuo carrello");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.TEXT);
        add(titolo, BorderLayout.NORTH);

        righePanel.setLayout(new BoxLayout(righePanel, BoxLayout.Y_AXIS));
        righePanel.setOpaque(false);
        final JScrollPane scroll = new JScrollPane(righePanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        add(creaPannelloCheckout(), BorderLayout.SOUTH);

        carrello.addListener(this::aggiornaVista);
        aggiornaVista();
    }

    private void aggiornaVista() {
        righePanel.removeAll();

        if (carrello.isEmpty()) {
            final JLabel vuoto = new JLabel("Il carrello è vuoto. Aggiungi qualche ricetta dal catalogo!");
            vuoto.setForeground(AppTheme.TEXT_MUTED);
            vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
            righePanel.add(vuoto);
        }

        BigDecimal totaleStimato = BigDecimal.ZERO;
        for (final Carrello.Riga riga : carrello.getRighe()) {
            righePanel.add(creaRigaCarrello(riga));
            righePanel.add(Box.createVerticalStrut(10));
            totaleStimato = totaleStimato.add(
                    riga.getRicetta().getPrezzoRicetta().multiply(BigDecimal.valueOf(riga.getQuantita())));
        }

        totaleLabel.setText(String.format("Totale stimato: %.2f € (lo sconto finale è calcolato alla conferma)",
                totaleStimato));
        confermaButton.setEnabled(!carrello.isEmpty());

        righePanel.revalidate();
        righePanel.repaint();
    }

    private RoundedCardPanel creaRigaCarrello(final Carrello.Riga riga) {
        final RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(12, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        final JLabel testo = new JLabel(String.format("%s  ×  %d   (%.2f € cad.)",
                riga.getRicetta().getNomeRicetta(), riga.getQuantita(), riga.getRicetta().getPrezzoRicetta()));
        testo.setFont(AppTheme.FONT_BODY);
        card.add(testo, BorderLayout.CENTER);

        final JButton rimuoviButton = new JButton("Rimuovi");
        rimuoviButton.setForeground(AppTheme.ERROR);
        rimuoviButton.setBorderPainted(false);
        rimuoviButton.setContentAreaFilled(false);
        rimuoviButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rimuoviButton.addActionListener(e -> carrello.rimuovi(riga.getRicetta().getCodiceRicetta()));
        card.add(rimuoviButton, BorderLayout.EAST);

        return card;
    }

    private JPanel creaPannelloCheckout() {
        final RoundedCardPanel card = new RoundedCardPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        totaleLabel.setFont(AppTheme.FONT_BUTTON);
        totaleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totaleLabel);
        card.add(Box.createVerticalStrut(10));

        final JPanel rigaNote = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rigaNote.setOpaque(false);
        noteField.setPreferredSize(new Dimension(300, 28));
        rigaNote.add(new JLabel("Note (opzionale):"));
        rigaNote.add(noteField);
        rigaNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(rigaNote);
        card.add(Box.createVerticalStrut(10));

        confermaButton.setFont(AppTheme.FONT_BUTTON);
        confermaButton.setBackground(AppTheme.PRIMARY);
        confermaButton.setForeground(Color.WHITE);
        confermaButton.setFocusPainted(false);
        confermaButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        confermaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confermaButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        confermaButton.addActionListener(e -> eseguiConferma());
        card.add(confermaButton);
        card.add(Box.createVerticalStrut(6));

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);

        return card;
    }

    // U9 - Il trigger trg_ordini_verifica_indirizzo può bloccare l'ordine se
    // l'utente non ha un indirizzo di spedizione impostato: gestiamo quel
    // caso con un messaggio dedicato, non generico.
    private void eseguiConferma() {
        confermaButton.setEnabled(false);
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Conferma in corso...");

        final String note = noteField.getText().trim();
        final var righeCarrello = carrello.toMappaQuantita();

        new SwingWorker<Long, Void>() {
            @Override
            protected Long doInBackground() throws Exception {
                return ordineController.registraOrdine(
                        utenteCorrente.getCodiceUtente(), note.isEmpty() ? null : note, righeCarrello);
            }

            @Override
            protected void done() {
                try {
                    final long codiceOrdine = get();
                    statusLabel.setForeground(AppTheme.SUCCESS);
                    statusLabel.setText("Ordine #" + codiceOrdine + " confermato!");
                    carrello.svuota();
                    noteField.setText("");
                } catch (final Exception ex) {
                    confermaButton.setEnabled(true);
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Impossibile confermare l'ordine: verifica di avere un "
                            + "indirizzo di spedizione impostato.");
                }
            }
        }.execute();
    }
}