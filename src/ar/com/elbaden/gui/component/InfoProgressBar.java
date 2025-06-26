package ar.com.elbaden.gui.component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static java.awt.RenderingHints.*;

public class InfoProgressBar extends JProgressBar {

    private Color borderColor;
    private Color background;
    private Color foreground;
    private Color selectionBackground;
    private Color selectionForeground;
    private boolean showingError;

    public InfoProgressBar() {
        setStringPainted(true);
        findColors();
    }

    @Override
    protected void paintBorder(Graphics g) {
        if (isBorderPainted()) {
            if (showingError && borderColor != null) {
                Border border = getBorder();
                if (border == null) {
                    return;
                }
                Insets insets = border.getBorderInsets(this);
                Color original = g.getColor();
                g.setColor(borderColor);
                g.drawRect(0, 0, getWidth() - insets.left, getHeight() - insets.top);
                g.setColor(original);
            } else {
                super.paintBorder(g);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showingError) {
            if (selectionBackground == null || selectionForeground == null) {
                return;
            }
            if (g instanceof Graphics2D g2d) {
                if (isStringPainted()) {
                    if (getString() != null) {
                        FontMetrics metrics = g2d.getFontMetrics();
                        float x = calculateStringWidth(metrics);
                        float y = calculateStringHeight(metrics);
                        // pinto el fondo completo
                        g2d.setColor(background);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        // pinto el texto
                        g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
                        g2d.setColor(selectionForeground);
                        g2d.drawString(getString(), x, y);
                        // calculo el ancho visible del progreso
                        int progressWidth = calculateProgressWidth();
                        Shape progressShape = g2d.getClip(); // preservo el lienzo original
                        // pinto el fondo que representa el progreso
                        g2d.setColor(selectionBackground);
                        g2d.fillRect(0, 0, progressWidth, getHeight());
                        // pinto una "capa" para el texto
                        g2d.clipRect(0, 0, progressWidth, getHeight());
                        // pinto el texto para esa "capa"
                        g2d.setColor(foreground);
                        g2d.drawString(getString(), x, y);
                        // restauro el lienzo original
                        g2d.setClip(progressShape);
                    }
                }
            }
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        findColors();
        repaint();
    }

    private void findColors() {
        borderColor = null;
        background = getBackground();
        foreground = getForeground();
        selectionBackground = null;
        selectionForeground = null;
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        UIDefaults defaults = lookAndFeel.getDefaults();
        if ("Metal".equals(lookAndFeel.getName())) {
            // colores por defecto
            String metalBorderKey = "OptionPane.errorDialog.titlePane.foreground";
            String metalErrorBg = "OptionPane.errorDialog.titlePane.background";
            String metalErrorFg = "OptionPane.errorDialog.titlePane.foreground";
            String metalSelectionBg = "OptionPane.errorDialog.border.background";
            String metalSelectionFg = "OptionPane.messageForeground";
            borderColor = defaults.getColor(metalBorderKey);
            background = defaults.getColor(metalErrorBg);
            foreground = defaults.getColor(metalErrorFg);
            selectionBackground = defaults.getColor(metalSelectionBg);
            selectionForeground = defaults.getColor(metalSelectionFg);
        } else if ("Nimbus".equals(lookAndFeel.getName())) {
            borderColor = defaults.getColor("NimbusRed");
        }
    }

    private float calculateStringHeight(FontMetrics metrics) {
        return ((float) (getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
    }

    private float calculateStringWidth(FontMetrics metrics) {
        if (getString() == null) {
            return 0f;
        }
        return (float) (getWidth() - metrics.stringWidth(getString())) / 2;
    }

    private int calculateProgressWidth() {
        double total = (getMaximum() - getMinimum()); // tengo en cuenta los límites
        return (int) ((getWidth() * (getValue() - getMinimum())) / total);
    }

    public void setShowingError(boolean showingError) {
        this.showingError = showingError;
    }

}
