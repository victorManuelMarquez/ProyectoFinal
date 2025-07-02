package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DisplayPane extends JTextPane {

    private static final Logger LOGGER = Logger.getLogger(DisplayPane.class.getName());
    public static final String INFO_STYLE = "infoStyle";
    public static final String ERROR_STYLE = "errorStyle";
    private final int rows;
    private final int cols;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public DisplayPane(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        // registro cada estilo
        Style style = addStyle(INFO_STYLE, null);
        StyleConstants.setForeground(style, Color.BLUE);
        style = addStyle(ERROR_STYLE, null);
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
            StringBuilder builder = new StringBuilder(text);
            // busco valores especiales
            List<File> files = findFiles(text);
            files.forEach(file -> simplify(builder, file));
            // inserto el texto
            StyledDocument document = getStyledDocument();
            int offset = document.getLength();
            Style style = styleName != null ? getStyle(styleName) : null;
            document.insertString(offset, builder.toString(), style);
            moveCaretPosition(document.getLength());
            // aplico estilo a los valores encontrados
            files.forEach(file -> updateStyle(file, getStyle(INFO_STYLE)));
        } catch (BadLocationException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private void updateStyle(File file, Style style) {
        StyledDocument document = getStyledDocument();
        String text = getText();
        String fileName = file.getName();
        do {
            int index = text.indexOf(fileName);
            int length = fileName.length() - 1 + index;
            document.setCharacterAttributes(index, length, style, true);
            text = text.substring(index, length);
        } while (text.contains(fileName));
    }

    private void simplify(StringBuilder content, File file) {
        String filePath = file.getPath();
        int start = content.indexOf(filePath);
        int end = start + filePath.length();
        content.replace(start, end, file.getName());
    }

    private List<File> findFiles(String value) {
        String[] lines = value.split(System.lineSeparator());
        List<File> files = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(File.separator)) {
                for (String split : line.split(" ")) {
                    File file = new File(split);
                    if (file.isFile() || file.isDirectory()) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    protected void addMargins(Dimension size, Insets insets) {
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
    }

}
