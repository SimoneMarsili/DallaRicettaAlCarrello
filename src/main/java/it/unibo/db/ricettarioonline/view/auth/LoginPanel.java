package it.unibo.db.ricettarioonline.view.auth;

import it.unibo.db.ricettarioonline.controller.LoginController;
import it.unibo.db.ricettarioonline.model.Utente;
import it.unibo.db.ricettarioonline.view.MainFrame;
import it.unibo.db.ricettarioonline.view.components.RoundedCardPanel;
import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Optional;

// Schermata di accesso: mostra login o registrazione, alternabili tramite
// un CardLayout interno. Ogni chiamata che tocca il DB (login/registrazione)
// gira in un SwingWorker, per non bloccare il thread grafico di Swing.
public class LoginPanel extends JPanel {

    private final LoginController loginController = new LoginController();
    private final CardLayout formLayout = new CardLayout();
    private final JPanel formCards = new JPanel(formLayout);

    private static final String FORM_LOGIN = "FORM_LOGIN";
    private static final String FORM_REGISTRAZIONE = "FORM_REGISTRAZIONE";

    public LoginPanel(final MainFrame parent) {
        super(new GridBagLayout());
        setBackground(AppTheme.BACKGROUND);

        final RoundedCardPanel card = new RoundedCardPanel(new java.awt.BorderLayout(0, 16));
        card.setPreferredSize(new Dimension(440, 520));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        card.add(creaIntestazione(), java.awt.BorderLayout.NORTH);

        formCards.setOpaque(false);
        formCards.add(creaFormLogin(parent), FORM_LOGIN);
        formCards.add(creaFormRegistrazione(parent), FORM_REGISTRAZIONE);
        card.add(formCards, java.awt.BorderLayout.CENTER);

        add(card);
    }

    private JPanel creaIntestazione() {
        final JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        final JLabel titolo = new JLabel("Dalla Ricetta Al Carrello");
        titolo.setFont(AppTheme.FONT_TITLE);
        titolo.setForeground(AppTheme.PRIMARY);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JLabel sottotitolo = new JLabel("Ricette, ordini e ingredienti in un unico posto");
        sottotitolo.setFont(AppTheme.FONT_SUBTITLE);
        sottotitolo.setForeground(AppTheme.TEXT_MUTED);
        sottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(sottotitolo);
        return header;
    }

    private JPanel creaFormLogin(final MainFrame parent) {
        final JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        final JTextField emailField = new JTextField();
        final JPasswordField passwordField = new JPasswordField();
        final JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.FONT_SUBTITLE);

        final JButton loginButton = creaBottonePrimario("Accedi");
        loginButton.addActionListener(e ->
                eseguiLogin(parent, emailField, passwordField, loginButton, statusLabel));

        final JButton passaARegistrazione = creaBottoneTestuale("Non hai un account? Registrati");
        passaARegistrazione.addActionListener(e -> formLayout.show(formCards, FORM_REGISTRAZIONE));

        final GridBagConstraints gbc = campoBaseGbc();
        form.add(campoConEtichetta("Email", emailField), gbc);
        gbc.gridy++;
        form.add(campoConEtichetta("Password", passwordField), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 0, 0);
        form.add(loginButton, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(statusLabel, gbc);
        gbc.gridy++;
        form.add(passaARegistrazione, gbc);

        return form;
    }

    private JPanel creaFormRegistrazione(final MainFrame parent) {
        final JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        final JTextField nomeField = new JTextField();
        final JTextField cognomeField = new JTextField();
        final JTextField emailField = new JTextField();
        final JPasswordField passwordField = new JPasswordField();
        final JTextField indirizzoField = new JTextField();
        final JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.FONT_SUBTITLE);

        final JButton registratiButton = creaBottonePrimario("Crea account");
        registratiButton.addActionListener(e -> eseguiRegistrazione(parent, nomeField, cognomeField,
                emailField, passwordField, indirizzoField, registratiButton, statusLabel));

        final JButton passaALogin = creaBottoneTestuale("Hai già un account? Accedi");
        passaALogin.addActionListener(e -> formLayout.show(formCards, FORM_LOGIN));

        final GridBagConstraints gbc = campoBaseGbc();
        form.add(campoConEtichetta("Nome", nomeField), gbc);
        gbc.gridy++;
        form.add(campoConEtichetta("Cognome", cognomeField), gbc);
        gbc.gridy++;
        form.add(campoConEtichetta("Email", emailField), gbc);
        gbc.gridy++;
        form.add(campoConEtichetta("Password", passwordField), gbc);
        gbc.gridy++;
        form.add(campoConEtichetta("Indirizzo di spedizione (opzionale)", indirizzoField), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 0, 0);
        form.add(registratiButton, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(statusLabel, gbc);
        gbc.gridy++;
        form.add(passaALogin, gbc);

        return form;
    }

    // U1 + login - Chiamata bloccante spostata in un SwingWorker: doInBackground
    // gira su un thread separato (qui è sicuro fare JDBC), done() torna sul
    // thread grafico per aggiornare l'interfaccia.
    private void eseguiLogin(final MainFrame parent, final JTextField emailField,
            final JPasswordField passwordField, final JButton loginButton, final JLabel statusLabel) {

        final String email = emailField.getText().trim();
        final char[] password = passwordField.getPassword();

        if (email.isEmpty() || password.length == 0) {
            statusLabel.setForeground(AppTheme.ERROR);
            statusLabel.setText("Inserisci email e password.");
            Arrays.fill(password, '\0');
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Accesso in corso...");

        new SwingWorker<Optional<Utente>, Void>() {
            @Override
            protected Optional<Utente> doInBackground() throws Exception {
                try {
                    return loginController.login(email, new String(password));
                } finally {
                    Arrays.fill(password, '\0'); // non tenere la password in chiaro in memoria più del necessario
                }
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    final Optional<Utente> utente = get();
                    if (utente.isPresent()) {
                        parent.loginSucceeded(utente.get());
                    } else {
                        statusLabel.setForeground(AppTheme.ERROR);
                        statusLabel.setText("Credenziali non valide.");
                    }
                } catch (final Exception ex) {
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Errore di accesso al database.");
                }
            }
        }.execute();
    }

    // U1 - Stesso pattern di eseguiLogin: SwingWorker per non bloccare la UI.
    private void eseguiRegistrazione(final MainFrame parent, final JTextField nomeField,
            final JTextField cognomeField, final JTextField emailField, final JPasswordField passwordField,
            final JTextField indirizzoField, final JButton registratiButton, final JLabel statusLabel) {

        final String nome = nomeField.getText().trim();
        final String cognome = cognomeField.getText().trim();
        final String email = emailField.getText().trim();
        final char[] password = passwordField.getPassword();
        final String indirizzo = indirizzoField.getText().trim();

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.length == 0) {
            statusLabel.setForeground(AppTheme.ERROR);
            statusLabel.setText("Compila tutti i campi obbligatori.");
            Arrays.fill(password, '\0');
            return;
        }

        registratiButton.setEnabled(false);
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        statusLabel.setText("Registrazione in corso...");

        new SwingWorker<Optional<Utente>, Void>() {
            @Override
            protected Optional<Utente> doInBackground() throws Exception {
                try {
                    loginController.registraUtente(nome, cognome, email, new String(password),
                            indirizzo.isEmpty() ? null : indirizzo);
                    // Dopo la registrazione, effettuiamo subito il login per
                    // ottenere l'oggetto Utente completo (con CodiceUtente).
                    return loginController.login(email, new String(password));
                } finally {
                    Arrays.fill(password, '\0');
                }
            }

            @Override
            protected void done() {
                registratiButton.setEnabled(true);
                try {
                    final Optional<Utente> utente = get();
                    if (utente.isPresent()) {
                        parent.loginSucceeded(utente.get());
                    } else {
                        statusLabel.setForeground(AppTheme.ERROR);
                        statusLabel.setText("Registrazione riuscita, ma il login automatico è fallito.");
                    }
                } catch (final Exception ex) {
                    statusLabel.setForeground(AppTheme.ERROR);
                    statusLabel.setText("Errore: email già registrata o dati non validi.");
                }
            }
        }.execute();
    }

    private GridBagConstraints campoBaseGbc() {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        return gbc;
    }

    private JPanel campoConEtichetta(final String etichettaTesto, final JTextField campo) {
        final JPanel gruppo = new JPanel();
        gruppo.setOpaque(false);
        gruppo.setLayout(new BoxLayout(gruppo, BoxLayout.Y_AXIS));

        final JLabel etichetta = new JLabel(etichettaTesto);
        etichetta.setFont(AppTheme.FONT_SUBTITLE);
        etichetta.setForeground(AppTheme.TEXT_MUTED);
        etichetta.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setFont(AppTheme.FONT_BODY);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        gruppo.add(etichetta);
        gruppo.add(campo);
        return gruppo;
    }

    private JButton creaBottonePrimario(final String testo) {
        final JButton button = new JButton(testo);
        button.setFont(AppTheme.FONT_BUTTON);
        button.setBackground(AppTheme.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return button;
    }

    private JButton creaBottoneTestuale(final String testo) {
        final JButton button = new JButton(testo);
        button.setFont(AppTheme.FONT_SUBTITLE);
        button.setForeground(AppTheme.PRIMARY);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        return button;
    }
}