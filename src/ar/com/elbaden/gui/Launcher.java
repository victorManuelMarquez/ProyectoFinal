package ar.com.elbaden.gui;

import ar.com.elbaden.gui.component.DisplayPane;
import ar.com.elbaden.gui.window.MainFrame;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Launcher extends SwingWorker<Void, String> implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
    private final DisplayPane displayPane;
    private final Window ancestor;
    private final Cursor defaultCursor;
    private final Timer countdown;
    private final int totalSeconds = 30;
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
        List<Callable<String>> normalList = List.of(
                new CallLoadSettings(),
                new CallListSettings(),
                new CallChangeLook(ancestor),
                new CallApplyFonts(ancestor),
                new CallListFontFamilies()
        );
        try {
            processList(normalList);
        } catch (Exception e) {
            publishError(e);
            List<Callable<String>> firstRunList = List.of(
                    new CallRestoreSettings(),
                    new CallListSettings()
            );
            processList(firstRunList);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(chunk -> {
            String newLine = chunk.concat(System.lineSeparator());
            displayPane.appendText(newLine);
        });
    }

    @Override
    protected void done() {
        ancestor.setCursor(defaultCursor);
        try {
            Object ignore = get();
            MainFrame.createAndShow();
            ancestor.dispose();
        } catch (Exception e) {
            // manejo del error
            publishError(e);
            countdown.start();
        }
    }

    private void processList(List<Callable<String>> callables) throws Exception {
        int item = 0;
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            for (Callable<String> callableTask : callables) {
                Future<String> future = service.submit(callableTask);
                String result = future.get();
                item++;
                LOGGER.finest(result);
                publish(result);
                setProgress(calculateProgress(item, callables.size()));
            }
        }
    }

    private int calculateProgress(int value, int total) {
        return value * 100 / total;
    }

    private String buildCountdownMessage(int value) {
        String pattern = App.messages.getString("infoProgressBar.remainingSeconds");
        return MessageFormat.format(pattern, value);
    }

    private void publishError(Exception exception) {
        LOGGER.severe(exception.getMessage());
        String cause = findCause(exception.getCause(), exception.getMessage());
        String message = (cause == null) ? exception.getMessage() : cause;
        SwingUtilities.invokeLater(() -> displayPane.appendErrorText(message.concat(System.lineSeparator())));
    }

    private String findCause(Throwable cause, String message) {
        if (cause == null) {
            return message;
        }
        return findCause(cause.getCause(), cause.getMessage());
    }

    public void stop() {
        cancel(true);
        countdown.stop();
    }

}
