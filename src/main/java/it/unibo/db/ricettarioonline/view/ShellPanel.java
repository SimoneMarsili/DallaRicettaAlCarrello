package it.unibo.db.ricettarioonline.view;

import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.view.admin.AdminPanel;
import it.unibo.db.ricettarioonline.view.carrello.Carrello;
import it.unibo.db.ricettarioonline.view.carrello.CarrelloPanel;
import it.unibo.db.ricettarioonline.view.carrello.StoricoOrdiniPanel;
import it.unibo.db.ricettarioonline.view.catalogo.CatalogoPanel;
import it.unibo.db.ricettarioonline.view.catalogo.PubblicaRicettaPanel;
import it.unibo.db.ricettarioonline.view.catalogo.VantaggiPanel;
import it.unibo.db.ricettarioonline.view.report.ClassifichePanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Contenitore principale post-login: sidebar di navigazione a sinistra +
// area contenuti a destra (con un proprio CardLayout). Le voci di menu
// cambiano in base al Ruolo dell'utente loggato.
public class ShellPanel extends JPanel {

    private static final int SIDEBAR_WIDTH = 220;
    private static final int MENU_BUTTON_HEIGHT = 44;

    private static final String CARD_CATALOGO = "CARD_CATALOGO";
    private static final String CARD_PUBBLICA_RICETTA = "CARD_PUBBLICA_RICETTA";
    private static final String CARD_VANTAGGI = "CARD_VANTAGGI";
    private static final String CARD_CARRELLO = "CARD_CARRELLO";
    private static final String CARD_STORICO = "CARD_STORICO";
    private static final String CARD_CLASSIFICHE = "CARD_CLASSIFICHE";
    private static final String CARD_AMMINISTRAZIONE = "CARD_AMMINISTRAZIONE";

    private final MainFrame parent;
    private final Utente utente;

    private final Carrello carrello = new Carrello();

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    private JButton bottoneSelezionato;

    public ShellPanel(final MainFrame parent, final Utente utente) {
        super(new BorderLayout());
        this.parent = parent;
        this.utente = utente;
        setBackground(AppTheme.BACKGROUND);

        add(creaSidebar(), BorderLayout.WEST);
        add(creaAreaContenuti(), BorderLayout.CENTER);
    }

    private JPanel creaSidebar() {
        final JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setBackground(AppTheme.PRIMARY);
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 18, 24, 18));

        final JLabel titolo = new JLabel("Dalla Ricetta Al Carrello");
        titolo.setForeground(Color.WHITE);
        titolo.setFont(AppTheme.FONT_SUBTITLE.deriveFont(java.awt.Font.BOLD, 18f));
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(titolo);
        sidebar.add(Box.createVerticalStrut(28));

        sidebar.add(creaBottoneMenu("Catalogo ricette", CARD_CATALOGO));
        sidebar.add(creaBottoneMenu("Pubblica ricetta", CARD_PUBBLICA_RICETTA));
        sidebar.add(creaBottoneMenu("Vantaggi attivi", CARD_VANTAGGI)); 
        sidebar.add(creaBottoneMenu("Carrello", CARD_CARRELLO));
        sidebar.add(creaBottoneMenu("Storico ordini", CARD_STORICO));
        sidebar.add(creaBottoneMenu("Classifiche", CARD_CLASSIFICHE));

        if ("ADMIN".equals(utente.getRuolo())) {
            sidebar.add(Box.createVerticalStrut(16));
            sidebar.add(creaBottoneMenu("Amministrazione", CARD_AMMINISTRAZIONE));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(creaBoxUtente());

        return sidebar;
    }

    private JButton creaBottoneMenu(final String testo, final String nomeCard) {
        final JButton button = new JButton(testo);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, MENU_BUTTON_HEIGHT));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        button.setFont(AppTheme.FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(AppTheme.PRIMARY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                if (button != bottoneSelezionato) {
                    button.setBackground(AppTheme.PRIMARY_HOVER);
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                if (button != bottoneSelezionato) {
                    button.setBackground(AppTheme.PRIMARY);
                }
            }
        });

        button.addActionListener(e -> {
            selezionaBottone(button);
            contentLayout.show(contentPanel, nomeCard);
        });

        if (bottoneSelezionato == null) {
            selezionaBottone(button);
        }

        return button;
    }

    private void selezionaBottone(final JButton button) {
        if (bottoneSelezionato != null) {
            bottoneSelezionato.setBackground(AppTheme.PRIMARY);
        }
        bottoneSelezionato = button;
        bottoneSelezionato.setBackground(AppTheme.PRIMARY_HOVER);
    }

    private JPanel creaBoxUtente() {
        final JPanel box = new JPanel(new BorderLayout(8, 4));
        box.setOpaque(false);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        final JLabel nome = new JLabel(utente.getNome() + " " + utente.getCognome());
        nome.setForeground(Color.WHITE);
        nome.setFont(AppTheme.FONT_BUTTON);

        final JButton logoutButton = new JButton("Esci");
        logoutButton.setFont(AppTheme.FONT_SUBTITLE);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.addActionListener(e -> parent.logout());

        box.add(nome, BorderLayout.NORTH);
        box.add(logoutButton, BorderLayout.SOUTH);
        return box;
    }

    private JPanel creaAreaContenuti() {
        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        contentPanel.add(new CatalogoPanel(utente, carrello), CARD_CATALOGO);
        contentPanel.add(new PubblicaRicettaPanel(utente), CARD_PUBBLICA_RICETTA);
        contentPanel.add(new VantaggiPanel(), CARD_VANTAGGI);
        contentPanel.add(new CarrelloPanel(utente, carrello), CARD_CARRELLO);
        contentPanel.add(new StoricoOrdiniPanel(utente), CARD_STORICO);
        contentPanel.add(new ClassifichePanel(utente, carrello), CARD_CLASSIFICHE);
        contentPanel.add(new AdminPanel(), CARD_AMMINISTRAZIONE);

        return contentPanel;
    }
}