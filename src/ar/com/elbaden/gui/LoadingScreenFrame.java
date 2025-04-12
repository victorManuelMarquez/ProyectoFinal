package ar.com.elbaden.gui;

import ar.com.elbaden.task.LaunchAppWorker;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoadingScreenFrame extends JFrame implements PropertyChangeListener {

    private final JProgressBar progressBar;
    private final JTextPane outputTextPane;

    private LoadingScreenFrame() throws HeadlessException {
        // ajustes
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // si el tema actual soporta personalización de ventanas
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        outputTextPane = new JTextPane();

        JScrollPane scrollPane = new JScrollPane(outputTextPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        getContentPane().add(scrollPane);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        getContentPane().add(progressBar, BorderLayout.SOUTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            getProgressBar().setValue((Integer) evt.getNewValue());
        } else if ("cursor".equals(evt.getPropertyName())) {
            getOutputTextPane().setCursor((Cursor) evt.getNewValue());
        }
    }

    public static void createAndShow() {
        LoadingScreenFrame frame = new LoadingScreenFrame();
        LaunchAppWorker worker = new LaunchAppWorker(frame.getOutputTextPane());
        worker.addPropertyChangeListener(frame);
        frame.addWindowListener(worker);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JTextPane getOutputTextPane() {
        return outputTextPane;
    }

}
