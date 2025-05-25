package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
import java.util.logging.SimpleFormatter;

public class LoadingScreen extends JFrame implements PropertyChangeListener {

    private static final Logger LOGGER = Logger.getGlobal();

    private final JProgressBar progressBar;

    private LoadingScreen(ResourceBundle messages) throws HeadlessException {
        String title = messages.getString("loadingScreen.title");
        setTitle(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JTextPane infoPane = new JTextPane();
        infoPane.setEditable(false);
        infoPane.setFocusable(false);
        infoPane.setCaretColor(infoPane.getBackground());
        infoPane.getCaret().setBlinkRate(0);
        infoPane.setOpaque(false);
        infoPane.setBackground(new Color(255, 255, 255, 0));
        infoPane.setPreferredSize(new Dimension(320, 144));
        JScrollPane infoScrollPane = new JScrollPane(infoPane);
        infoScrollPane.setBorder(null);
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

    public static void createAndShow(ResourceBundle messages) {
        LoadingScreen loadingScreen = new LoadingScreen(messages);
        loadingScreen.pack();
        loadingScreen.setLocationRelativeTo(null);
        loadingScreen.setVisible(true);
    }

    static class Launcher extends SwingWorker<Void, String> implements WindowListener {

        private final JTextPane outputPane;
        private final ResourceBundle messages;

        public Launcher(JTextPane outputPane, ResourceBundle messages) {
            this.outputPane = outputPane;
            this.messages = messages;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String starting = messages.getString("loadingScreen.task.starting");
            appendText(starting + System.lineSeparator(), null);
            File userHome = new File(System.getProperty("user.home"));
            List<CallableTask<?>> tasks = List.of(
                    new CreateMainFolderTask(userHome, messages),
                    new FileHandlerSetTask(new File(userHome, ".baden"), messages)
            );
            int item = 0;
            int total = tasks.size();
            for (CallableTask<?> task : tasks) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                try (ExecutorService service = Executors.newSingleThreadExecutor()) {
                    item++;
                    Future<?> result = service.submit(task);
                    try {
                        Object output = result.get();
                        if (output instanceof String line) {
                            appendText(line, null);
                            String newLine;
                            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
                            StyleConstants.setForeground(attributeSet, task.getTextColor());
                            if (task.getSymbol() != null) {
                                newLine = " " + task.getSymbol() + System.lineSeparator();
                                appendText(newLine, attributeSet);
                            }
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
        protected void done() {
            try {
                Void ignore = get();
                String finished = messages.getString("loadingScreen.task.finished");
                appendText(finished, null);
            } catch (Exception e) {
                appendText(e.getMessage(), null);
                LOGGER.severe(e.getMessage());
            }
        }

        private void appendText(String line, SimpleAttributeSet attributeSet) {
            StyledDocument document = outputPane.getStyledDocument();
            try {
                document.insertString(document.getLength(), line, attributeSet);
            } catch (BadLocationException e) {
                LOGGER.severe(e.getMessage());
            }
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

    static abstract class CallableTask<T> implements Callable<T> {

        public static final String OK_SYMBOL = "✔";
        public static final String ERROR_SYMBOL = "❌";

        private String symbol;
        private Color textColor;

        public CallableTask(String symbol, Color textColor) {
            this.symbol = symbol;
            this.textColor = textColor;
        }

        public CallableTask() {
            this(OK_SYMBOL, Color.GREEN);
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Color getTextColor() {
            return textColor;
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }

    }

    static class CreateMainFolderTask extends CallableTask<String> {

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
                        setSymbol(ERROR_SYMBOL);
                        setTextColor(Color.RED);
                        String message = messages.getString("loadingScreen.task.appFolder.creationFailed");
                        return MessageFormat.format(message, file.getPath());
                    }
                }
            } catch (RuntimeException e) {
                throw new ExecutionException(e);
            }
        }

    }

    static class FileHandlerSetTask extends CallableTask<FileHandler> {

        private final File userHome;
        private final ResourceBundle messages;

        public FileHandlerSetTask(File userHome, ResourceBundle messages) {
            this.userHome = userHome;
            this.messages = messages;
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
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                LOGGER.addHandler(fileHandler);
                LOGGER.info(messages.getString("log.info.fileHandlerSet"));
                return fileHandler;
            } catch (RuntimeException e) {
                throw new ExecutionException(e);
            }
        }

    }

}
