package it.unibo.db.ricettarioonline.view.components;

import it.unibo.db.ricettarioonline.view.theme.AppTheme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

// Un pannello bianco con angoli arrotondati e un bordo sottile, per far
// "galleggiare" i contenuti sopra lo sfondo colorato di AppTheme.BACKGROUND.
public class RoundedCardPanel extends JPanel {

    public RoundedCardPanel(final LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(AppTheme.SURFACE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.RADIUS_PANEL, AppTheme.RADIUS_PANEL);

        g2.setColor(AppTheme.BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.RADIUS_PANEL, AppTheme.RADIUS_PANEL);

        g2.dispose();
        super.paintComponent(g);
    }
}