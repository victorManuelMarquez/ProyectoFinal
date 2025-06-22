package ar.com.elbaden.gui;

import ar.com.elbaden.gui.component.DisplayPane;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Launcher extends SwingWorker<Void, String> {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
    private final DisplayPane displayPane;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public Launcher(DisplayPane displayPane) {
        this.displayPane = displayPane;
    }

    @Override
    protected Void doInBackground() throws Exception {
        publishMessage(App.messages.getString("loading"), null);
        int total = 0;
        try {
            // rutina normal
            List<CheckPoint> checkPoints = List.of(
                    new CheckingDirectory(Settings.getAppFolder()),
                    new CheckingFile(Settings.getXSDFile()),
                    new CheckingFile(Settings.getXMLFile()),
                    new ReadingSettings()
            );
            total += checkPoints.size();
            processCheckPoints(checkPoints, total);
        } catch (Exception e) {
            // manejo del error
            LOGGER.severe(e.getMessage());
            publishError(e);
            // rutina de restauración
            publishMessage(App.messages.getString("retrying"), Color.BLUE);
            List<CheckPoint> checkPoints = List.of(
                    new RestoringDirectory(Settings.getAppFolder())
            );
            total += checkPoints.size();
            processCheckPoints(checkPoints, total);
        }
        publishMessage(App.messages.getString("finished"), null);
        return null;
    }

    @Override
    protected void done() {
        try {
            Object ignore = get();
        } catch (Exception e) {
            // manejo del error
            LOGGER.severe(e.getMessage());
            publishError(e);
        }
    }

    protected void processCheckPoints(List<CheckPoint> checkPoints, int total) throws Exception {
        int item = 0;
        ExecutorService service = Executors.newSingleThreadExecutor();
        try (service) {
            for (CheckPoint checkPoint : checkPoints) {
                Future<String> future = service.submit(checkPoint);
                try {
                    String result = future.get();
                    publishMessage(result, null);
                    LOGGER.log(checkPoint.getLogRecord());
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

    protected void publishMessage(String message, Color foregroundColor) {
        String line = message.contains(System.lineSeparator()) ? message : message.concat(System.lineSeparator());
        SwingUtilities.invokeLater(() -> {
            displayPane.setStyleForeground(foregroundColor);
            displayPane.appendText(line);
        });
    }

    protected void publishError(Exception exception) {
        String message = findCause(exception.getCause(), exception.getMessage()).concat(System.lineSeparator());
        publishMessage(message, Color.RED);
    }

}
