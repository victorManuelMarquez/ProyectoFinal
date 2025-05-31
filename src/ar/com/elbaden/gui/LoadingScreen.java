package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class LoadingScreen extends JFrame {

    private LoadingScreen(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        JTextPane outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setFocusable(false);
        outputPane.getCaret().setVisible(false);
        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setPreferredSize(new Dimension(240, 144));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(scrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        // eventos
        addWindowListener(new WindowAdapter() {

            private final Loader loader = new Loader(outputPane);

            @Override
            public void windowOpened(WindowEvent e) {
                loader.addPropertyChangeListener(evt -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        Integer progress = (Integer) evt.getNewValue();
                        progressBar.setValue(progress);
                    } else if ("countdown".equals(evt.getPropertyName())) {
                        Integer remaining = (Integer) evt.getNewValue();
                        progressBar.setString(String.format("Cerrando en %d segundos...", remaining));
                    }
                });
                loader.execute();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                loader.cancel(true);
                if (loader.getCountdown().isRunning()) {
                    loader.getCountdown().stop();
                }
            }

        });
    }

    public static void createAndShow() {
        LoadingScreen frame = new LoadingScreen("Cargando...");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static class Loader extends SwingWorker<Void, Void> implements ActionListener {

        private final JTextPane textPane;
        private final Timer countdown;
        private final int totalSeconds = 30;
        private int second;

        public Loader(JTextPane textPane) {
            this.textPane = textPane;
            countdown = new Timer(1000, this);
            second = totalSeconds;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int old = second;
            second--;
            firePropertyChange("progress",
                    calculateProgress(old, totalSeconds),
                    calculateProgress(second, totalSeconds));
            firePropertyChange("countdown", old, second);
            if (second <= 0) {
                countdown.stop();
                Window ancestor = SwingUtilities.getWindowAncestor(getTextPane());
                ancestor.dispose();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            appendText("Cargando la información necesaria...", null, true);
            List<CheckPoint<?>> routineCheckPoints = List.of(
                    new FindAppFolder(),
                    new LoadProperties()
            );
            int total = routineCheckPoints.size();
            int item = 0;
            try (ExecutorService service = Executors.newFixedThreadPool(routineCheckPoints.size())) {
                List<Future<?>> results = new ArrayList<>();
                for (CheckPoint<?> checkPoint : routineCheckPoints) {
                    Future<?> result = service.submit(checkPoint);
                    results.add(result);
                }
                for (Future<?> result : results) {
                    try {
                        appendText(result.get().toString(), null, true);
                        item++;
                        setProgress(calculateProgress(item, total));
                    } catch (InterruptedException | ExecutionException e) {
                        appendText(e.getMessage(), null, true);
                        service.shutdownNow();
                        throw e;
                    }
                }
            } catch (Exception e) {
                List<CheckPoint<?>> firstRunTasks = List.of(
                        new CreateAppFolder(),
                        new SaveProperties(),
                        new LoadProperties()
                );
                total = item + firstRunTasks.size();
                for (CheckPoint<?> checkPoint : firstRunTasks) {
                    try (ExecutorService service = Executors.newSingleThreadExecutor()) {
                        Future<?> result = service.submit(checkPoint);
                        try {
                            appendText(result.get().toString(), styledContent(checkPoint), true);
                            item++;
                            setProgress(calculateProgress(item, total));
                        } catch (InterruptedException | ExecutionException ex) {
                            appendText(ex.getMessage(), styledContent(checkPoint), true);
                            service.shutdownNow();
                            throw ex;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                Void ignore = get();
                appendText("Finalizado correctamente.", null, false);
            } catch (Exception e) {
                SimpleAttributeSet errorAttributeSet = new SimpleAttributeSet();
                StyleConstants.setForeground(errorAttributeSet, Color.RED);
                appendText(e.getMessage(), errorAttributeSet, false);
                e.printStackTrace(System.err);
            }
            countdown.start();
        }

        private int calculateProgress(int actual, int total) {
            return actual * 100 / total;
        }

        private AttributeSet styledContent(CheckPoint<?> checkPoint) {
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            if (checkPoint.getForegroundColor() != null) {
                StyleConstants.setForeground(simpleAttributeSet, checkPoint.getForegroundColor());
            }
            return simpleAttributeSet;
        }

        private void appendText(String text, AttributeSet attributeSet, boolean newLine) {
            StyledDocument styledDocument = getTextPane().getStyledDocument();
            try {
                int offset = styledDocument.getLength();
                styledDocument.insertString(offset, text, attributeSet);
                if (newLine) {
                    offset = styledDocument.getLength();
                    styledDocument.insertString(offset, System.lineSeparator(), attributeSet);
                }
            } catch (BadLocationException e) {
                e.printStackTrace(System.err);
            }
        }

        public JTextPane getTextPane() {
            return textPane;
        }

        public Timer getCountdown() {
            return countdown;
        }

    }

    static abstract class CheckPoint<T> implements Callable<T> {

        private Color foregroundColor;

        public Color getForegroundColor() {
            return foregroundColor;
        }

        public void setForegroundColor(Color foregroundColor) {
            this.foregroundColor = foregroundColor;
        }

    }

    static class FindAppFolder extends CheckPoint<File> {

        @Override
        public File call() throws Exception {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            File appFolder;
            try {
                appFolder = new File(System.getProperty("user.home"), App.FOLDER);
                if (!appFolder.exists()) {
                    throw new FileNotFoundException(appFolder.getPath());
                }
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return appFolder;
        }

    }

    static class CreateAppFolder extends CheckPoint<File> {

        @Override
        public File call() throws Exception {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            File appFolder;
            try {
                appFolder = new File(System.getProperty("user.home"), App.FOLDER);
                if (!appFolder.exists()) {
                    if (appFolder.mkdir()) {
                        setForegroundColor(Color.GREEN);
                    } else {
                        throw new IOException(appFolder.getPath());
                    }
                }
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return appFolder;
        }

    }

    static class LoadProperties extends CheckPoint<Properties> {

        @Override
        public Properties call() throws Exception {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Properties properties;
            try {
                File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
                File iniFile = new File(appFolder, "settings.ini");
                try (FileInputStream inputStream = new FileInputStream(iniFile)) {
                    properties = new Properties();
                    properties.load(inputStream);
                }
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return properties;
        }

    }

    static class SaveProperties extends CheckPoint<Properties> {

        @Override
        public Properties call() throws Exception {
            Properties properties;
            try {
                File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
                File iniFile = new File(appFolder, "settings.ini");
                try (FileOutputStream outputStream = new FileOutputStream(iniFile)) {
                    properties = new Properties();
                    properties.setProperty("fontFamily", "Dialog");
                    properties.setProperty("fontSize", "12");
                    properties.store(outputStream, "Configuración");
                }
                setForegroundColor(Color.GREEN);
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return properties;
        }

    }

}
