package ar.com.elbaden.gui;

import ar.com.elbaden.task.AppChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    static class LSWindowEvents extends WindowAdapter implements PropertyChangeListener, ActionListener {

        private final JFrame root;
        private final JProgressBar actualProgress;

        private final AppChecker checker;
        private final Timer countdown;

        private final int totalSeconds = 15;
        private int second = totalSeconds;

        public LSWindowEvents(JTextArea contentArea, JProgressBar actualProgress) {
            root = (JFrame) SwingUtilities.getRoot(contentArea);
            this.actualProgress = actualProgress;
            checker = new AppChecker(root, contentArea);
            checker.addPropertyChangeListener(this);
            countdown = new Timer(1000, this);
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
        public void actionPerformed(ActionEvent e) {
            String localFormattedCountdown = "El programa se cerrará en %d segundos..." + System.lineSeparator();
            getActualProgress().setValue(second * 100 / totalSeconds);
            getActualProgress().setString(String.format(localFormattedCountdown, second));
            second--;
            if (second < 0) {
                getCountdown().stop();
                getRoot().dispose();
            }
        }

        @Override
        public void windowOpened(WindowEvent e) {
            getChecker().execute();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (!getChecker().isDone())
                getChecker().cancel(true);
            if (getCountdown().isRunning())
                getCountdown().stop();
            getRoot().dispose();
        }

        public JFrame getRoot() {
            return root;
        }

        public JProgressBar getActualProgress() {
            return actualProgress;
        }

        protected AppChecker getChecker() {
            return checker;
        }

        public Timer getCountdown() {
            return countdown;
        }

    }

}
