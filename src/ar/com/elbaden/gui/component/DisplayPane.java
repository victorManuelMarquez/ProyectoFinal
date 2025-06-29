package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.logging.Logger;

public class DisplayPane extends JTextPane {

    private static final Logger LOGGER = Logger.getLogger(DisplayPane.class.getName());
    public static final String INFO_FG_STYLE = "infoForeground";
    public static final String ERROR_FG_STYLE = "errorForeground";
    private final int rows;
    private final int cols;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public DisplayPane(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        // registro cada estilo
        Style style = addStyle(INFO_FG_STYLE, null);
        StyleConstants.setForeground(style, Color.BLUE);
        style = addStyle(ERROR_FG_STYLE, null);
        StyleConstants.setForeground(style, Color.RED);

        // ajustes
        calculatePreferredSize();
        setEditable(false);
        setFocusable(false);
        setCaretColor(new Color(0, 0, 0, 0)); // "cursor" transparente
        getCaret().setBlinkRate(0); // el "cursor" ya no parpadeará
    }

    public void calculatePreferredSize() {
        Dimension size = getPreferredSize();
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
        setPreferredSize(size);
    }

    public void appendText(String text, String styleName) {
        if (text == null) {
            return;
        }
        try {
            StyledDocument document = getStyledDocument();
            int offset = document.getLength();
            Style style = styleName != null ? getStyle(styleName) : null;
            document.insertString(offset, text, style);
            moveCaretPosition(document.getLength());
        } catch (BadLocationException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    protected void addMargins(Dimension size, Insets insets) {
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
    }

}
