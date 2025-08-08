package ar.com.baden.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.text.MessageFormat;
import java.util.List;

public class LaunchWorker extends SwingWorker<Void, String> {

    private final Window ancestor;
    private final Cursor cursor;
    private final StyledDocument document;
    private final Style errorStyle;

    public LaunchWorker(JTextPane textPane) {
        ancestor = SwingUtilities.getWindowAncestor(textPane);
        cursor = ancestor.getCursor();
        document = textPane.getStyledDocument();
        Style boldStyle = document.addStyle("boldStyle", null);
        StyleConstants.setBold(boldStyle, true);
        errorStyle = document.addStyle("errorStyle", boldStyle);
        StyleConstants.setForeground(errorStyle, Color.RED);
    }

    @Override
    protected Void doInBackground() throws Exception {
        ancestor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int totalSeconds = 10;
        int seconds = 0;
        int countdown = totalSeconds;
        while (countdown >= 0) {
            Thread.sleep(1000); // un segundo
            String pattern = "cerrando en: {0,choice,1#{0,number,integer} segundo|1<{0,number,integer} segundos}";
            String message = MessageFormat.format(pattern, countdown);
            countdown--;
            publish(message);
            setProgress(seconds * 100 / totalSeconds);
            seconds++;
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(str -> appendText(str, null));
    }

    @Override
    protected void done() {
        ancestor.setCursor(cursor);
        try {
            Object ignore = get();
            ancestor.setVisible(false);
            ancestor.dispose();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            publishError(e);
        }
    }

    private void appendText(String value, Style style) {
        if (!value.endsWith(System.lineSeparator())) {
            value = value.concat(System.lineSeparator());
        }
        try {
            int offset = document.getLength();
            document.insertString(offset, value, style);
        } catch (BadLocationException e) {
            e.printStackTrace(System.err);
        }
    }

    protected void publishError(Exception e) {
        String message = null;
        for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
            if (cause.getMessage() != null) {
                message = cause.getMessage();
            }
        }
        if (message != null) {
            appendText(message, errorStyle);
        } else {
            appendText(e.getMessage(), errorStyle);
        }
    }

}
