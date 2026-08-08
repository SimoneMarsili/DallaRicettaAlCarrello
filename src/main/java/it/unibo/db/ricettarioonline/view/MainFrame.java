package it.unibo.db.ricettarioonline.view;

import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.view.auth.LoginPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;

// Finestra principale: tiene un CardLayout con una "carta" per ogni schermata
// dell'applicazione. Le view non si aprono mai da sole: passano sempre da qui
// per cambiare schermata, tramite i metodi pubblici sotto.
public class MainFrame extends JFrame {

    private static final String VIEW_LOGIN = "VIEW_LOGIN";
    private static final String VIEW_HOME = "VIEW_HOME";

    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private Utente utenteCorrente;
    private JPanel shellPanel;

    public MainFrame() {
        setTitle("RicettarioOnline");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1080, 720);
        setLocationRelativeTo(null); // centra la finestra sullo schermo

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(AppTheme.BACKGROUND);

        initViews();
        add(mainContainer);
    }

    private void initViews() {
        mainContainer.add(new LoginPanel(this), VIEW_LOGIN);
        cardLayout.show(mainContainer, VIEW_LOGIN);
    }

    // Chiamato da LoginPanel dopo un login (o registrazione) riuscito: crea la
    // shell principale (sidebar + contenuti) per l'utente appena autenticato.
    public void loginSucceeded(final Utente utente) {
        this.utenteCorrente = utente;

        if (shellPanel != null) {
            mainContainer.remove(shellPanel);
        }
        shellPanel = new ShellPanel(this, utente);
        mainContainer.add(shellPanel, VIEW_HOME);

        mainContainer.revalidate();
        mainContainer.repaint();
        cardLayout.show(mainContainer, VIEW_HOME);
    }

    // Chiamato da ShellPanel quando l'utente clicca "Esci": torna al login,
    // dimenticando l'utente corrente.
    public void logout() {
        this.utenteCorrente = null;
        cardLayout.show(mainContainer, VIEW_LOGIN);
    }

    // Cambia schermata mostrando la "carta" con il nome indicato. Utile per
    // future navigazioni dirette (es. da un pulsante di ShellPanel verso una
    // schermata a tutto schermo esterna alla shell, se mai servisse).
    public void changeView(final String viewName) {
        cardLayout.show(mainContainer, viewName);
    }

    public static void main(final String[] args) {
        AppTheme.install();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}