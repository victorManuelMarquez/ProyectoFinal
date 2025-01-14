package ar.com.elbaden.gui;

import ar.com.elbaden.task.AppChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoadingScreen extends JFrame {

    public LoadingScreen(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        installComponents();
    }

    private void installComponents() {
        BorderLayout defaultLayout = (BorderLayout) getContentPane().getLayout();
        defaultLayout.setVgap(8);

        add(new JLabel(), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setDisabledTextColor(UIManager.getColor("TextArea.foreground"));
        textArea.setMargin(new Insets(0, 8, 0, 8));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(null);
        textArea.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(480, 240));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.SOUTH);

        addWindowListener(new LSWindowEvents(textArea, progressBar));
    }

    public static void createAndShow() {
        String localTitle = "Iniciando programa";
        try {
            LoadingScreen window = new LoadingScreen(localTitle);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        } catch (RuntimeException runtimeException) {
            // ignore
        }
    }

    static class LSWindowEvents extends WindowAdapter implements PropertyChangeListener, Runnable {

        private final JFrame root;
        private final JTextArea publisher;
        private final JProgressBar actualProgress;

        private final AppChecker checker;
        private final Thread countdown;

        public LSWindowEvents(JTextArea contentArea, JProgressBar actualProgress) {
            root = (JFrame) SwingUtilities.getRoot(contentArea);
            this.publisher = contentArea;
            this.actualProgress = actualProgress;
            checker = new AppChecker(root, contentArea);
            checker.addPropertyChangeListener(this);
            countdown = new Thread(this, "countdown");
            countdown.setDaemon(true);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("countdown".equals(evt.getPropertyName())) {
                getCountdown().start();
            } else if ("progress".equals(evt.getPropertyName())) {
                getActualProgress().setValue(getChecker().getProgress());
            } else if ("progressIndeterminate".equals(evt.getPropertyName())) {
                getActualProgress().setIndeterminate((Boolean) evt.getNewValue());
            }
        }

        @Override
        public void run() {
            int totalSeconds = 15;
            String localFormattedCountdown = "El programa se cerrará en %d segundos..." + System.lineSeparator();
            for (int second = totalSeconds; second != 0; second--) {
                getActualProgress().setValue(second * 100 / totalSeconds);
                getPublisher().append(String.format(localFormattedCountdown, second));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            getRoot().dispose();
        }

        @Override
        public void windowOpened(WindowEvent e) {
            getChecker().execute();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (!getChecker().isDone())
                getChecker().cancel(true);
            if (getCountdown().isAlive())
                getCountdown().interrupt();
            getRoot().dispose();
        }

        public JFrame getRoot() {
            return root;
        }

        public JTextArea getPublisher() {
            return publisher;
        }

        public JProgressBar getActualProgress() {
            return actualProgress;
        }

        protected AppChecker getChecker() {
            return checker;
        }

        public Thread getCountdown() {
            return countdown;
        }

    }

}
