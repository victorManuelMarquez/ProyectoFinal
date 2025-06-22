package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.logging.Logger;

public class DisplayPane extends JTextPane {

    private static final Logger LOGGER = Logger.getLogger(DisplayPane.class.getName());
    private final SimpleAttributeSet attributeSet;
    private final int rows;
    private final int cols;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public DisplayPane(int rows, int cols) {
        this.attributeSet = new SimpleAttributeSet();
        this.rows = rows;
        this.cols = cols;
        // ajustes
        setEditable(false);
        setFocusable(false);
        setCaretColor(new Color(0, 0, 0, 0)); // "cursor" transparente
        getCaret().setBlinkRate(0); // el "cursor" ya no parpadeará
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        FontMetrics metrics = getFontMetrics(getFont());
        size.height = metrics.getHeight() * rows;
        size.width = metrics.charWidth('m') * cols;
        Insets margins = getInsets();
        if (margins != null) {
            addMargins(size, margins);
        }
        Border border = getBorder();
        if (border != null) {
            addMargins(size, border.getBorderInsets(this));
        }
        return size;
    }

    public void appendText(String text) {
        try {
            StyledDocument document = getStyledDocument();
            int offset = document.getLength();
            document.insertString(offset, text, attributeSet);
            moveCaretPosition(document.getLength());
        } catch (BadLocationException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    protected void addMargins(Dimension size, Insets insets) {
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
    }

    public void setStyleForeground(Color color) {
        StyleConstants.setForeground(attributeSet, color == null ? getForeground() : color);
    }

}
