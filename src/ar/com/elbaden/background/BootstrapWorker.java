package ar.com.elbaden.background;

import ar.com.elbaden.background.task.CheckFileHandler;
import ar.com.elbaden.background.task.CheckTempDir;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
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
    private final StyledDocument document;
    private final ResourceBundle messages;

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public BootstrapWorker(JTextPane textPane) {
        cursor = textPane.getCursor();
        document = textPane.getStyledDocument();
        messages = ResourceBundle.getBundle(App.RESOURCE_BUNDLE_BASE_NAME);
    }

    @Override
    protected Void doInBackground() {
        firePropertyChange("cursor", cursor, waitCursor);
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            List<Callable<String>> checkpointsList = new ArrayList<>();
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
                    publish(result.get());
                    setProgress(item * 100 / total);
                } catch (InterruptedException | ExecutionException e) {
                    GLOBAL_LOGGER.severe(e.getLocalizedMessage());
                    service.shutdownNow();
                }
            }
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
            cancel(true);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            try {
                chunk = (chunk.endsWith(System.lineSeparator()) ? chunk : chunk.concat(System.lineSeparator()));
                getDocument().insertString(getDocument().getLength(), chunk, null);
            } catch (BadLocationException e) {
                GLOBAL_LOGGER.warning(e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void done() {
        firePropertyChange("cursor", waitCursor, cursor);
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

    public StyledDocument getDocument() {
        return document;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

}
