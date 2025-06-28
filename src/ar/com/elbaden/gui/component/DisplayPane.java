package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.*;
import java.text.MessageFormat;
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
        calculatePreferredSize();
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        setEditorKit(htmlEditorKit);
        StyleSheet css = htmlEditorKit.getStyleSheet();
        css.addRule("a { text-decoration: none; }");
        setEditable(false);
        setFocusable(false);
        setCaretColor(new Color(0, 0, 0, 0)); // "cursor" transparente
        getCaret().setBlinkRate(0); // el "cursor" ya no parpadeará
        // eventos
        addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (e.getURL() == null) {
                    String desc = e.getDescription();
                    File file = new File(desc);
                    boolean isOpened = false;
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop desktop = Desktop.getDesktop();
                            if (file.exists()) {
                                desktop.open(file);
                                isOpened = true;
                            }
                        } catch (IOException | RuntimeException ex) {
                            LOGGER.severe(ex.getMessage());
                        }
                    }
                    if (!isOpened) {
                        try {
                            Runtime runtime = Runtime.getRuntime();
                            String tool = "xdg-open";
                            String[] commandLine = new String[] {tool, file.getAbsolutePath()};
                            Process process = runtime.exec(commandLine);
                            showErrorStream(process).start();
                            int signal = process.waitFor();
                            switch (signal) {
                                case 1 -> logMessage("commandSyntaxError", signal);
                                case 2 -> logMessage("commandFileNotFound", signal, file);
                                case 3 -> logMessage("commandToolNotFound", signal, tool);
                                case 4 -> logMessage("commandActionFailed", signal);
                            }
                        } catch (IOException | InterruptedException ex) {
                            LOGGER.severe(ex.getMessage());
                        }
                    }
                }
            }
        });
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

    protected Thread showErrorStream(Process process) {
        return new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String newLine = line + System.lineSeparator();
                    SwingUtilities.invokeLater(() -> appendTextColor(newLine, Color.RED));
                }
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        });
    }

    private void logMessage(String pattern, Object... parameters) {
        String message = App.messages.getString(pattern);
        LOGGER.severe(MessageFormat.format(message, parameters));
    }

    public void appendText(String text) {
        if (text == null) {
            return;
        }
        try {
            StyledDocument document = getStyledDocument();
            int offset = document.getLength();
            document.insertString(offset, text, attributeSet);
            moveCaretPosition(document.getLength());
        } catch (BadLocationException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void appendLink(File file) {
        if (file == null) {
            return;
        }
        try {
            HTMLDocument document = (HTMLDocument) getDocument();
            Element element = document.getCharacterElement(document.getLength());
            String htmlText = "<a href=\"" + file.getPath() + "\">" + file.getName() + "</a>";
            document.insertAfterEnd(element, htmlText);
        } catch (BadLocationException | IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void appendTextColor(String text, Color foreground) {
        StyleConstants.setForeground(attributeSet, foreground == null ? getForeground() : foreground);
        appendText(text);
    }

    protected void addMargins(Dimension size, Insets insets) {
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
    }

}
