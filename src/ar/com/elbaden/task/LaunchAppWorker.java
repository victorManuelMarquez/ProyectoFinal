package ar.com.elbaden.task;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LaunchAppWorker extends SwingWorker<Void, String> implements WindowListener {

    private final Cursor cursor, waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private final StyledDocument styledDocument;
    private final List<Callable<Boolean>> tasksList;

    public LaunchAppWorker(JTextPane outputTextPane) {
        cursor = outputTextPane.getCursor();
        styledDocument = outputTextPane.getStyledDocument();
        tasksList = List.of(initiateMySQLDriver());
    }

    @Override
    protected Void doInBackground() {
        firePropertyChange("cursor", cursor, waitCursor);
        try (ExecutorService executorService = Executors.newFixedThreadPool(tasksList.size())) {
            List<Future<Boolean>> results = new ArrayList<>();
            for (Callable<Boolean> task : tasksList) {
                results.add(executorService.submit(task));
            }
            executorService.shutdown();
            int totalTasksSize = tasksList.size();
            int actualProgress = 1;
            for (Future<Boolean> result : results) {
                if (result.get()) {
                    publish("Ok");
                } else {
                    publish("Falló");
                }
                setProgress(actualProgress * 100 / totalTasksSize);
                actualProgress++;
            }
        } catch (ExecutionException | InterruptedException e) {
            cancel(true);
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            try {
                getStyledDocument().insertString(getStyledDocument().getLength(), chunk, null);
            } catch (BadLocationException badLocationException) {
                // ignore
            }
        }
    }

    @Override
    protected void done() {
        firePropertyChange("cursor", waitCursor, cursor);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        execute();
    }

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {
        cancel(true);
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    private Callable<Boolean> initiateMySQLDriver() {
        return () -> {
            Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
            clazz.getConstructor().newInstance();
            return true;
        };
    }

    public StyledDocument getStyledDocument() {
        return styledDocument;
    }

}
