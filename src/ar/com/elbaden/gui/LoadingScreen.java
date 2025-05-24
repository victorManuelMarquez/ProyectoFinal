package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadingScreen extends JFrame implements PropertyChangeListener {

    private static final Logger LOGGER = Logger.getGlobal();

    private final JProgressBar progressBar;

    private LoadingScreen() throws HeadlessException {
        ResourceBundle messages = ResourceBundle.getBundle(App.MESSAGES);
        String title = messages.getString("loadingScreen.title");
        setTitle(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JTextPane infoPane = new JTextPane();
        infoPane.setEditable(false);
        infoPane.setFocusable(false);
        infoPane.setCaretColor(infoPane.getBackground());
        infoPane.getCaret().setBlinkRate(0);
        infoPane.setPreferredSize(new Dimension(360, 144));
        JScrollPane infoScrollPane = new JScrollPane(infoPane);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        getContentPane().add(infoScrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);
        Launcher launcher = new Launcher(infoPane, messages);
        launcher.addPropertyChangeListener(this);
        addWindowListener(launcher);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            Integer progressValue = (Integer) evt.getNewValue();
            progressBar.setValue(progressValue);
        }
    }

    public static void createAndShow() {
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.pack();
        loadingScreen.setLocationRelativeTo(null);
        loadingScreen.setVisible(true);
    }

    static class Launcher extends SwingWorker<Void, String> implements WindowListener {

        private final JTextPane outputPane;
        private final ResourceBundle messages;
        private final StringBuilder outputBuilder;

        public Launcher(JTextPane outputPane, ResourceBundle messages) {
            this.outputPane = outputPane;
            this.messages = messages;
            outputBuilder = new StringBuilder(outputPane.getText());
        }

        @Override
        protected Void doInBackground() throws Exception {
            File userHome = new File(System.getProperty("user.home"));
            List<Callable<?>> callables = List.of(
                    new CreateMainFolderTask(userHome, messages),
                    new FileHandlerSetTask(new File(userHome, ".baden"))
            );
            int item = 0;
            int total = callables.size();
            for (Callable<?> callable : callables) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                try (ExecutorService service = Executors.newSingleThreadExecutor()) {
                    item++;
                    Future<?> result = service.submit(callable);
                    try {
                        Object output = result.get();
                        if (output instanceof String line) {
                            String newLine = line + System.lineSeparator();
                            publish(newLine);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        service.shutdownNow();
                        throw e;
                    }
                    setProgress(item * 100 / total);
                }
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                appendText(chunk);
            }
        }

        @Override
        protected void done() {
            StringBuilder builder = new StringBuilder(outputPane.getText());
            try {
                Void ignore = get();
                String finished = messages.getString("loadingScreen.task.finished");
                builder.append(finished);
            } catch (Exception e) {
                builder.append(e.getMessage());
                LOGGER.severe(e.getMessage());
            }
            outputPane.setText(builder.toString());
        }

        private void appendText(String line) {
            outputBuilder.append(line);
            outputPane.setText(outputBuilder.toString());
        }

        @Override
        public void windowOpened(WindowEvent e) {
            execute();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            cancel(true);
        }

        @Override
        public void windowClosed(WindowEvent e) {}

        @Override
        public void windowIconified(WindowEvent e) {}

        @Override
        public void windowDeiconified(WindowEvent e) {}

        @Override
        public void windowActivated(WindowEvent e) {}

        @Override
        public void windowDeactivated(WindowEvent e) {}

    }

    static class CreateMainFolderTask implements Callable<String> {

        private final File userHome;
        private final ResourceBundle messages;

        public CreateMainFolderTask(File userHome, ResourceBundle messages) {
            this.userHome = userHome;
            this.messages = messages;
        }

        @Override
        public String call() throws Exception {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                File file = new File(userHome, ".baden");
                if (file.exists()) {
                    return messages.getString("loadingScreen.task.appFolder.found");
                } else {
                    if (file.mkdir()) {
                        String message = messages.getString("loadingScreen.task.appFolder.creationSuccessfully");
                        return MessageFormat.format(message, file.getName());
                    } else {
                        String message = messages.getString("loadingScreen.task.appFolder.creationFailed");
                        return MessageFormat.format(message, file.getPath());
                    }
                }
            } catch (RuntimeException e) {
                throw new ExecutionException(e);
            }
        }

    }

    static class FileHandlerSetTask implements Callable<FileHandler> {

        private final File userHome;

        public FileHandlerSetTask(File userHome) {
            this.userHome = userHome;
        }

        @Override
        public FileHandler call() throws Exception {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                File logFile = new File(userHome, "log.txt");
                FileHandler fileHandler = new FileHandler(logFile.getPath(), false);
                fileHandler.setLevel(Level.INFO);
                LOGGER.addHandler(fileHandler);
                return fileHandler;
            } catch (RuntimeException e) {
                throw new ExecutionException(e);
            }
        }

    }

}
