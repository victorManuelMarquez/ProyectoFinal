package ar.com.elbaden.gui;

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

        JTextArea textArea = new JTextArea();
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setDisabledTextColor(UIManager.getColor("TextArea.foreground"));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(480, 360));
        add(scrollPane);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.SOUTH);

        addWindowListener(new LSWindowEvents(textArea, progressBar));
    }

    public static void createAndShow() {
        String localTitle = "Iniciando...";
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
        private final JTextArea contentArea;
        private final JProgressBar actualProgress;

        public LSWindowEvents(JTextArea contentArea, JProgressBar actualProgress) {
            this.contentArea = contentArea;
            this.actualProgress = actualProgress;
            root = (JFrame) SwingUtilities.getRoot(contentArea);
        }

        @Override
        public void windowOpened(WindowEvent e) {
            getContentArea().append(getRoot().getTitle() + System.lineSeparator());
            getActualProgress().setIndeterminate(true);
        }

        @Override
        public void windowClosing(WindowEvent e) {
            getRoot().dispose();
        }

        public JFrame getRoot() {
            return root;
        }

        public JTextArea getContentArea() {
            return contentArea;
        }

        public JProgressBar getActualProgress() {
            return actualProgress;
        }

    }

}
