package ar.com.elbaden.gui;

import ar.com.elbaden.gui.component.DisplayPane;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Launcher extends SwingWorker<Void, String> implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
    private final DisplayPane displayPane;
    private final Window ancestor;
    private final Timer countdown;
    private final Cursor defaultCursor;
    private final int totalSeconds = 16;
    private int seconds = totalSeconds;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public Launcher(DisplayPane displayPane) {
        this.displayPane = displayPane;
        ancestor = SwingUtilities.getWindowAncestor(displayPane);
        countdown = new Timer(1000, this);
        defaultCursor = ancestor.getCursor();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int old = seconds;
        seconds--;
        int oldProgress = calculateProgress(old, totalSeconds);
        int newProgress = calculateProgress(seconds, totalSeconds);
        firePropertyChange("progress", oldProgress, newProgress);
        String oldMessage = buildCountdownMessage(old);
        String newMessage = buildCountdownMessage(seconds);
        firePropertyChange("countdown", oldMessage, newMessage);
        if (seconds == 0) {
            countdown.stop();
            ancestor.dispose();
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        ancestor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        publish(App.messages.getString("loading"));
        int total = 0;
        try {
            // rutina normal
            List<CheckPoint> checkPoints = List.of(
                    new CheckingDirectory(Settings.getAppFolder()),
                    new CheckingFile(Settings.getXMLFile()),
                    new ReadingSettings(),
                    new ApplyingLookAndFeel(ancestor),
                    new ApplyingFonts(ancestor)
            );
            total += checkPoints.size();
            processCheckPoints(checkPoints, total);
        } catch (Exception e) {
            // manejo del error
            LOGGER.severe(e.getMessage());
            publishError(e);
            // rutina de restauración
            publish(App.messages.getString("retrying"));
            List<CheckPoint> checkPoints = List.of(
                    new RestoringDirectory(Settings.getAppFolder()),
                    new RestoringSettings(),
                    new ReadingSettings()
            );
            total = checkPoints.size();
            processCheckPoints(checkPoints, total);
        }
        publish(App.messages.getString("finished"));
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(chunk -> displayPane.appendText(chunk.concat(System.lineSeparator()), null));
    }

    @Override
    protected void done() {
        ancestor.setCursor(defaultCursor);
        try {
            Object ignore = get();
        } catch (Exception e) {
            // manejo del error
            LOGGER.severe(e.getMessage());
            publishError(e);
            countdown.start();
        }
    }

    public void stop() {
        cancel(true);
        countdown.stop();
    }

    protected void processCheckPoints(List<CheckPoint> checkPoints, int total) throws Exception {
        int item = 0;
        ExecutorService service = Executors.newSingleThreadExecutor();
        try (service) {
            for (CheckPoint checkPoint : checkPoints) {
                Future<String> future = service.submit(checkPoint);
                try {
                    String result = future.get();
                    publish(result);
                    LOGGER.finest(result);
                    item++;
                    setProgress(calculateProgress(item, total));
                } catch (InterruptedException | ExecutionException e) {
                    service.shutdownNow();
                    throw e;
                }
            }
        } finally {
            service.shutdown();
        }
    }

    protected int calculateProgress(int value, int total) {
        return value * 100 / total;
    }

    protected String findCause(Throwable cause, String message) {
        if (cause == null) {
            return message;
        } else {
            return findCause(cause.getCause(), cause.getMessage());
        }
    }

    protected void publishError(Exception exception) {
        String cause = findCause(exception.getCause(), exception.getMessage());
        String message = cause == null ? exception.getMessage() : cause;
        SwingUtilities.invokeLater(() -> displayPane.appendText(message, DisplayPane.ERROR_STYLE));
    }

    protected String buildCountdownMessage(int second) {
        String pattern = App.messages.getString("finishedClosingIn");
        return MessageFormat.format(pattern, second);
    }

}
