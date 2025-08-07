package ar.com.baden.gui;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.List;

public class LaunchWorker extends SwingWorker<Void, String> {

    private final JTextArea textArea;
    private final Window ancestor;
    private final Cursor cursor;

    public LaunchWorker(JTextArea textArea) {
        this.textArea = textArea;
        ancestor = SwingUtilities.getWindowAncestor(textArea);
        cursor = ancestor.getCursor();
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
        chunks.forEach(this::appendNewLine);
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

    private void appendNewLine(String value) {
        if (value.endsWith(System.lineSeparator())) {
            textArea.append(value);
        } else {
            textArea.append(value.concat(System.lineSeparator()));
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
            appendNewLine(message);
        } else {
            appendNewLine(e.getMessage());
        }
    }

}
