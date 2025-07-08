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
    public static final String ERROR_STYLE = "errorStyle";
    private final int rows;
    private final int cols;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public DisplayPane(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        Style style = addStyle(ERROR_STYLE, null);
        StyleConstants.setForeground(style, Color.RED);

        // ajustes
        calculatePreferredSize();
        setEditable(false);
        setFocusable(false);
        setCaretColor(new Color(0, 0, 0, 0)); // "cursor" transparente
        getCaret().setBlinkRate(0); // el "cursor" ya no parpadeará
    }

    public void calculatePreferredSize() {
        FontMetrics metrics = getFontMetrics(getFont());
        Dimension dimension = getPreferredSize();
        dimension.height = metrics.getHeight() * rows;
        dimension.width = metrics.charWidth('m') * cols;
        includeMargins(dimension, getInsets());
        Border border = getBorder();
        if (border != null) {
            includeMargins(dimension, border.getBorderInsets(this));
        }
        setPreferredSize(dimension);
    }

    private void includeMargins(Dimension dimension, Insets margins) {
        if (margins != null) {
            dimension.height += margins.top + margins.bottom;
            dimension.width += margins.left + margins.right;
        }
    }

    public void appendStyledText(String text, String styleName) {
        if (text == null) {
            return;
        }
        StyledDocument styledDocument = getStyledDocument();
        Style style = (styleName == null) ? null : getStyle(styleName);
        int offset = styledDocument.getLength();
        try {
            styledDocument.insertString(offset, text, style);
            moveCaretPosition(styledDocument.getLength());
        } catch (BadLocationException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void appendText(String text) {
        appendStyledText(text, null);
    }

    public void appendErrorText(String errorText) {
        appendStyledText(errorText, ERROR_STYLE);
    }

}
