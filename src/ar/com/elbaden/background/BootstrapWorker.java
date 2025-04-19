package ar.com.elbaden.background;

import ar.com.elbaden.background.task.CheckApplyingRoboto;
import ar.com.elbaden.background.task.CheckFileHandler;
import ar.com.elbaden.background.task.CheckRobotoFont;
import ar.com.elbaden.background.task.CheckTempDir;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BootstrapWorker extends SwingWorker<Void, String> implements WindowListener {

    private final Cursor cursor, waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private final JTextPane textPane;
    private final ResourceBundle messages;
    private boolean isCheckSymbol = false;

    private static final String CHECK_SYMBOL = "✓", FAIL_SYMBOL = "✗";

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public BootstrapWorker(JTextPane textPane) {
        cursor = textPane.getCursor();
        this.textPane = textPane;
        messages = ResourceBundle.getBundle(App.MESSAGES);
    }

    @Override
    protected Void doInBackground() {
        firePropertyChange("cursor", cursor, waitCursor);
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            List<Callable<String>> checkpointsList = new ArrayList<>();
            checkpointsList.add(new CheckRobotoFont(getMessages()));
            checkpointsList.add(new CheckApplyingRoboto(getMessages(), getTextPane()));
            checkpointsList.add(new CheckTempDir());
            checkpointsList.add(new CheckFileHandler());
            Iterator<Callable<String>> checkpointIterator = checkpointsList.iterator();
            int total = checkpointsList.size();
            int item = 0;
            while (!isCancelled() && !service.isShutdown() && checkpointIterator.hasNext()) {
                Callable<String> checkpoint = checkpointIterator.next();
                item++;
                Future<String> result = service.submit(checkpoint);
                try {
                    String value = result.get();
                    isCheckSymbol = true;
                    publish(value);
                    setProgress(item * 100 / total);
                } catch (InterruptedException | ExecutionException e) {
                    service.shutdownNow();
                    isCheckSymbol = false;
                    throw e;
                }
            }
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
            // se cancela de momento
            cancel(true);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        StyledDocument styledDocument = getTextPane().getStyledDocument();
        AttributeSet defaultStyle = styledDocument.getStyle(StyleContext.DEFAULT_STYLE);
        StyleContext context = StyleContext.getDefaultStyleContext();
        int offset;
        for (String chunk : chunks) {
            try {
                offset = styledDocument.getLength();
                AttributeSet style;
                if (isCheckSymbol) {
                    style = context.addAttribute(defaultStyle, StyleConstants.Foreground, Color.GREEN);
                    styledDocument.insertString(offset, CHECK_SYMBOL + " ", style);
                } else {
                    style = context.addAttribute(defaultStyle, StyleConstants.Foreground, Color.RED);
                    styledDocument.insertString(offset, FAIL_SYMBOL + " ", style);
                }
                chunk = (chunk.endsWith(System.lineSeparator()) ? chunk : chunk.concat(System.lineSeparator()));
                styledDocument.insertString(styledDocument.getLength(), chunk, defaultStyle);
            } catch (BadLocationException e) {
                GLOBAL_LOGGER.warning(e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void done() {
        firePropertyChange("cursor", waitCursor, cursor);
        if (isCancelled()) {
            Window windowAncestor = SwingUtilities.getWindowAncestor(getTextPane());
            windowClosing(new WindowEvent(windowAncestor, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        GLOBAL_LOGGER.info(getMessages().getString("log.info.app.start"));
        execute();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        cancel(true);
        e.getWindow().dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        try {
            if (isCancelled()) {
                GLOBAL_LOGGER.info(getMessages().getString("log.info.worker.cancelled"));
            } else {
                GLOBAL_LOGGER.finest(getMessages().getString("log.finest.worker.done"));
            }
            // fin del programa
            GLOBAL_LOGGER.info(getMessages().getString("log.info.app.finished"));
        } catch (RuntimeException error) {
            GLOBAL_LOGGER.severe(error.getLocalizedMessage());
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    public JTextPane getTextPane() {
        return textPane;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

}
