package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Launcher extends SwingWorker<Void, Void> implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
    private final JTextPane displayPane;
    private final StyledDocument styledDocument;
    private final ResourceBundle messages;
    private final Window ancestor;
    private final Timer countdown;
    private final int totalSeconds = 11;
    private Color foregroundColor;
    private int seconds = totalSeconds;

    public Launcher(JTextPane displayPane) {
        this.displayPane = displayPane;
        styledDocument = displayPane.getStyledDocument();
        messages = ResourceBundle.getBundle(App.BUNDLE_NAME);
        ancestor = SwingUtilities.getWindowAncestor(displayPane);
        LOGGER.setResourceBundle(messages);
        foregroundColor = displayPane.getForeground();
        countdown = new Timer(1000, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int old = seconds;
        seconds--;
        firePropertyChange("progress",
                calculateProgress(old, totalSeconds),
                calculateProgress(seconds, totalSeconds));
        firePropertyChange("countdown", old, seconds);
        if (seconds == 0) {
            countdown.stop();
            ancestor.dispose();
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        LogRecord record = new LogRecord(Level.INFO, "launcher.starting");
        record.setResourceBundle(messages);
        LOGGER.log(record);
        String text = messages.getString(record.getMessage());
        appendText(text, null);
        File appFolder = new File(System.getProperty("user.home"), App.FOLDER_NAME);
        File xsdFile = new File(appFolder, Settings.XSD_FILE_NAME);
        File xslFile = new File(appFolder, Settings.XSL_FILE_NAME);
        File xmlFile = new File(appFolder, Settings.XML_FILE_NAME);
        try {
            List<CheckPoint> checkPoints = List.of(
                    new CheckingFile(messages, xsdFile),
                    new CheckingFile(messages, xslFile),
                    new CheckingFile(messages, xmlFile),
                    new LoadingSettings(messages, xsdFile, xslFile, xmlFile),
                    new ApplyingFonts(messages, ancestor),
                    new ApplyingTheme(messages, ancestor)
            );
            processCheckpoints(checkPoints);
        } catch (Exception e) {
            List<CheckPoint> checkPoints = List.of(
                    new RestoringSettingsFiles(messages, xsdFile, xslFile, xmlFile),
                    new LoadingSettings(messages, xsdFile, xslFile, xmlFile)
            );
            processCheckpoints(checkPoints);
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            Object ignore = get();
            MainFrame.createAndShow();
            ancestor.dispose();
        } catch (Exception e) {
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            applyForegroundStyle(simpleAttributeSet, foregroundColor);
            appendText(e.getMessage(), simpleAttributeSet);
            countdown.start();
        }
        LogRecord record = new LogRecord(Level.INFO, "launcher.finished");
        record.setResourceBundle(messages);
        LOGGER.log(record);
        String text = messages.getString(record.getMessage());
        appendText(text, null);
    }

    public void stopCountdown() {
        if (countdown.isRunning()) {
            countdown.stop();
        }
    }

    private void appendText(String value, AttributeSet attributeSet) {
        try {
            int offset = styledDocument.getLength();
            if (!value.endsWith(System.lineSeparator())) {
                value += System.lineSeparator();
            }
            styledDocument.insertString(offset, value, attributeSet);
            offset = styledDocument.getLength();
            displayPane.setCaretPosition(offset);
        } catch (BadLocationException | IllegalArgumentException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private void applyForegroundStyle(MutableAttributeSet attributeSet, Color color) {
        StyleConstants.setForeground(attributeSet, color == null ? displayPane.getForeground() : color);
    }

    private int calculateProgress(int value, int total) {
        return value * 100 / total;
    }

    private void processCheckpoints(List<CheckPoint> checkPoints) throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        int item = 0, total = checkPoints.size();
        try (service) {
            for (CheckPoint checkPoint : checkPoints) {
                Future<String> future = service.submit(checkPoint);
                String text = future.get();
                foregroundColor = checkPoint.getForegroundColor();
                checkPoint.setForegroundColor(displayPane.getForeground());
                applyForegroundStyle(checkPoint.getAttributeSet(), checkPoint.getForegroundColor());
                appendText(text, checkPoint.getAttributeSet());
                LOGGER.log(checkPoint.getRecord());
                item++;
                setProgress(calculateProgress(item, total));
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } finally {
            service.shutdown();
        }
    }

}
