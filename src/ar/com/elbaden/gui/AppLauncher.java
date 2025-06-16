package ar.com.elbaden.gui;

import ar.com.elbaden.gui.component.MainFrame;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class AppLauncher extends SwingWorker<Void, String> implements ActionListener {

    private final JTextArea textArea;
    private final Window ancestor;
    private final Timer countdown;
    private final List<LogRecord> records;
    private final int totalSeconds = 11;
    private int seconds = totalSeconds;

    public AppLauncher(JTextArea textArea) {
        this.textArea = textArea;
        ancestor = SwingUtilities.getWindowAncestor(textArea);
        countdown = new Timer(1000, this);
        records = new ArrayList<>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int old = seconds;
        seconds--;
        firePropertyChange("progress",
                calculateProgress(old, totalSeconds),
                calculateProgress(seconds, totalSeconds));
        firePropertyChange("countdown", countdownMessage(old), countdownMessage(seconds));
        if (seconds == 0) {
            countdown.stop();
            ancestor.dispose();
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        LogRecord initialRecord = new LogRecord(Level.INFO, App.MESSAGES.getString("appLauncher.starting"));
        records.add(initialRecord);
        App.LOGGER.log(initialRecord);
        File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
        File xsdFile = new File(appFolder, Settings.XSD_FILE_NAME);
        File xmlFile = new File(appFolder, Settings.XML_FILE_NAME);
        File xslFile = new File(appFolder, Settings.XSL_FILE_NAME);
        int progressValue = 0;
        try {
            List<CheckPoint<?>> checkPoints = List.of(
                    new CheckingAppFolder(appFolder),
                    new InstallingFileHandler(appFolder),
                    new CheckingXSDFile(xsdFile),
                    new CheckingXSLFile(xslFile),
                    new CheckingXMLFile(xmlFile),
                    new LoadingSettings(xsdFile, xslFile, xmlFile),
                    new CheckingTheme(),
                    new ApplyingTheme(ancestor),
                    new ApplyingFonts(ancestor)
            );
            processCheckPoint(checkPoints, progressValue);
        } catch (Exception e) {
            List<CheckPoint<?>> checkPoints = List.of(
                    new RestoringAppFolder(appFolder),
                    new InstallingFileHandler(appFolder),
                    new RestoringXSDFile(xsdFile),
                    new RestoringXSLFile(xslFile),
                    new RestoringXMLFile(xmlFile),
                    new LoadingSettings(xsdFile, xslFile, xmlFile)
            );
            processCheckPoint(checkPoints, progressValue);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(textArea::append);
    }

    @Override
    protected void done() {
        try {
            Void ignore = get();
            publishAllRecords();
            App.LOGGER.info(App.MESSAGES.getString("appLauncher.finished"));
            MainFrame.createAndShow(App.MESSAGES.getString("mainFrame.title"));
            ancestor.dispose();
        } catch (Exception e) {
            App.LOGGER.severe(e.getMessage());
            countdown.start();
        }
    }

    private void publishAllRecords() {
        for (Handler handler : App.LOGGER.getHandlers()) {
            if (handler instanceof FileHandler fileHandler) {
                records.forEach(fileHandler::publish);
            }
        }
    }

    private int calculateProgress(int value, int total) {
        return value * 100 / total;
    }

    private String countdownMessage(int second) {
        String message = App.MESSAGES.getString("appLauncher.format.countdownMessage");
        return MessageFormat.format(message, second);
    }

    private void processCheckPoint(List<CheckPoint<?>> checkPoints, int progress)
            throws ExecutionException, InterruptedException {
        int total = progress + checkPoints.size();
        int value = progress;
        ExecutorService service = Executors.newSingleThreadExecutor();
        try (service) {
            for (CheckPoint<?> checkPoint : checkPoints) {
                Future<?> future = service.submit(checkPoint);
                try {
                    Object result = future.get();
                    if (result instanceof String stringValue) {
                        records.add(new LogRecord(Level.FINE, stringValue));
                    } else if (result != null) {
                        records.add(new LogRecord(Level.FINE, result.toString()));
                        publish(result + System.lineSeparator());
                    }
                    value++;
                    setProgress(calculateProgress(value, total));
                } catch (InterruptedException | ExecutionException e) {
                    records.add(new LogRecord(Level.SEVERE, e.getMessage()));
                    publish(e.getMessage());
                    throw e;
                }
            }
        } finally {
            service.shutdownNow();
        }
    }

    public Timer getCountdown() {
        return countdown;
    }

}
