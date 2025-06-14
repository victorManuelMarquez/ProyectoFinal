package ar.com.elbaden.gui;

import ar.com.elbaden.gui.component.MainFrame;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class AppLauncher extends SwingWorker<Void, String> implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(AppLauncher.class.getName());
    private final JTextArea textArea;
    private final Window ancestor;
    private final Timer countdown;
    private final int totalSeconds = 11;
    private int seconds = totalSeconds;

    public AppLauncher(JTextArea textArea) {
        this.textArea = textArea;
        ancestor = SwingUtilities.getWindowAncestor(textArea);
        countdown = new Timer(1000, this);
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
        LOGGER.info(App.MESSAGES.getString("appLauncher.starting"));
        File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
        File xsdFile = new File(appFolder, Settings.XSD_FILE_NAME);
        File xmlFile = new File(appFolder, Settings.XML_FILE_NAME);
        int progressValue = 0;
        try {
            List<CheckPoint<?>> checkPoints = List.of(
                    new CheckingAppFolder(appFolder),
                    new InstallingFileHandler(appFolder),
                    new CheckingXSDFile(xsdFile),
                    new CheckingXMLFile(xmlFile),
                    new LoadingSettings(xsdFile, xmlFile),
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
                    new RestoringXMLFile(xmlFile),
                    new LoadingSettings(xsdFile, xmlFile)
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
            LOGGER.info(App.MESSAGES.getString("appLauncher.finished"));
            MainFrame.createAndShow(App.MESSAGES.getString("mainFrame.title"));
            ancestor.dispose();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            countdown.start();
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
                    publish(result.toString() + System.lineSeparator());
                    value++;
                    setProgress(calculateProgress(value, total));
                } catch (InterruptedException | ExecutionException e) {
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
