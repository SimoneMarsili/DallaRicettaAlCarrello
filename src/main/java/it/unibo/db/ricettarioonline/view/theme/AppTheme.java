package it.unibo.db.ricettarioonline.view.theme;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

// Raccoglie colori, font e impostazioni globali di Swing in un unico posto,
// così tutte le schermate condividono lo stesso stile senza ripeterlo.
public final class AppTheme {

    // Palette: toni caldi, coerenti con un tema "food" (arancio/terracotta come
    // accento, sfondo chiaro neutro). Cambiala pure se preferisci altri colori,
    // basta modificarli qui: si propagano a tutta l'app.
    public static final Color BACKGROUND = new Color(250, 247, 242);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_MUTED = new Color(243, 237, 228);

    public static final Color PRIMARY = new Color(214, 96, 43);       // arancio/terracotta
    public static final Color PRIMARY_HOVER = new Color(191, 82, 33);
    public static final Color ACCENT = new Color(87, 138, 87);        // verde salvia (richiama "vegano")

    public static final Color TEXT = new Color(35, 30, 26);
    public static final Color TEXT_MUTED = new Color(112, 102, 92);
    public static final Color BORDER = new Color(224, 214, 201);

    public static final Color SUCCESS = new Color(56, 142, 60);
    public static final Color ERROR = new Color(198, 60, 51);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 13);

    public static final int RADIUS_PANEL = 16;
    public static final int RADIUS_BUTTON = 10;

    // Palette dedicata al pannello amministratore: tono blu/indaco scuro,
    // nettamente diverso dall'arancio usato nella parte utente, per rendere
    // visivamente ovvio quando si è in "modalità amministratore".
    public static final Color ADMIN_BACKGROUND = new Color(235, 238, 245);
    public static final Color ADMIN_PRIMARY = new Color(52, 61, 110);
    public static final Color ADMIN_PRIMARY_HOVER = new Color(40, 48, 90);
    public static final Color ADMIN_ACCENT = new Color(196, 155, 60);

    private AppTheme() {
    }

    // Da chiamare una sola volta, all'avvio dell'applicazione (in main()),
    // prima di creare qualunque componente Swing: imposta i colori di default
    // usati anche dai componenti standard (JTextField, JScrollPane, ecc.),
    // così anche ciò che non personalizziamo esplicitamente resta coerente.
    public static void install() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("control", BACKGROUND);

        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Button.font", FONT_BUTTON);

        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
    }
}