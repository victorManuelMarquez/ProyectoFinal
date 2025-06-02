package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class LoadingScreen extends JFrame {

    private LoadingScreen(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        CustomTextPane outputPane = new CustomTextPane();
        outputPane.setRows(12);
        outputPane.setCols(36);
        outputPane.setEditable(false);
        outputPane.setFocusable(false);
        outputPane.getCaret().setVisible(false);
        JScrollPane scrollPane = new JScrollPane(outputPane);
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
        private final Window ancestor;
        private final Timer countdown;
        private final int totalSeconds = 30;
        private int second;

        public Loader(JTextPane textPane) {
            this.textPane = textPane;
            ancestor = SwingUtilities.getWindowAncestor(textPane);
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
                ancestor.dispose();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            appendText("Cargando la información necesaria...", null, true);
            int progress = 0;
            try {
                List<CheckPoint<?>> routineCheckPoints = List.of(
                        new FindAppFolder(),
                        new LoadSettings(),
                        new ApplyTheme(ancestor)
                );
                processCheckPoints(routineCheckPoints, progress);
            } catch (Exception e) {
                appendText(e.getMessage(), null, true);
                List<CheckPoint<?>> firstRunTasks = List.of(
                        new CreateAppFolder(),
                        new RestoreSettings(),
                        new LoadSettings()
                );
                processCheckPoints(firstRunTasks, progress);
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

        private void processCheckPoints(List<CheckPoint<?>> checkPoints, int progress)
                throws InterruptedException, ExecutionException {
            int total = progress + checkPoints.size();
            int item = progress;
            for (CheckPoint<?> checkPoint : checkPoints) {
                try (ExecutorService service = Executors.newSingleThreadExecutor()) {
                    Future<?> future = service.submit(checkPoint);
                    try {
                        appendText(future.get().toString(), styledContent(checkPoint), true);
                        item++;
                        setProgress(calculateProgress(item, total));
                    } catch (InterruptedException | ExecutionException e) {
                        service.shutdownNow();
                        throw e;
                    }
                }
            }
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
            StyledDocument styledDocument = textPane.getStyledDocument();
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

    static class LoadSettings extends CheckPoint<Settings> {

        @Override
        public Settings call() throws Exception {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
                File xmlFile = new File(appFolder, "settings.xml");
                App.settings = new Settings();
                App.settings.load(xmlFile);
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return App.settings;
        }

    }

    static class RestoreSettings extends CheckPoint<Settings> {

        @Override
        public Settings call() throws Exception {
            Settings settings;
            try {
                File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
                File xmlFile = new File(appFolder, "settings.xml");
                settings = Settings.getDefaults();
                settings.dump(xmlFile, 4);
                setForegroundColor(Color.GREEN);
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return settings;
        }

    }

    static class ApplyTheme extends CheckPoint<String> {

        private final Window window;

        public ApplyTheme(Window window) {
            this.window = window;
        }

        @Override
        public String call() throws Exception {
            LookAndFeel theme = UIManager.getLookAndFeel();
            try {
                if (!theme.getID().equals(App.settings.getThemeID())) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            UIManager.setLookAndFeel(App.settings.getThemeClass());
                            SwingUtilities.updateComponentTreeUI(window);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (Exception e) {
                setForegroundColor(Color.RED);
                throw new ExecutionException(e);
            }
            return App.settings.getThemeID();
        }

    }

}
