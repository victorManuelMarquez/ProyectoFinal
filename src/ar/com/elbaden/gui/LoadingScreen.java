package ar.com.elbaden.gui;

import ar.com.elbaden.task.AppChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoadingScreen extends JFrame {

    public LoadingScreen(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
        scrollPane.setPreferredSize(new Dimension(480, 360));
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

    static class LSWindowEvents extends WindowAdapter {

        private final JFrame root;

        private final AppChecker checker;

        public LSWindowEvents(JTextArea contentArea, JProgressBar actualProgress) {
            root = (JFrame) SwingUtilities.getRoot(contentArea);
            checker = new AppChecker(contentArea);
            checker.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    actualProgress.setValue(getChecker().getProgress());
                } else if ("everythingIsOk".equals(evt.getPropertyName())) {
                    getRoot().dispose();
                }
            });
        }

        @Override
        public void windowOpened(WindowEvent e) {
            getChecker().execute();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (!getChecker().isDone())
                getChecker().cancel(true);
            getRoot().dispose();
        }

        public JFrame getRoot() {
            return root;
        }

        protected AppChecker getChecker() {
            return checker;
        }

    }

}
