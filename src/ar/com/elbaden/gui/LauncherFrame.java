package ar.com.elbaden.gui;

import ar.com.elbaden.background.BootstrapWorker;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LauncherFrame extends JFrame implements PropertyChangeListener {

    private final JTextPane outputTextPane;
    private final JProgressBar progressBar;

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public LauncherFrame() throws HeadlessException {
        // ajustes
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }
        // localización
        ResourceBundle messages = ResourceBundle.getBundle(App.MESSAGES);
        setTitle(messages.getString("app.launcher.title"));
        // componentes
        outputTextPane = new JTextPane();
        outputTextPane.setEditable(false);
        outputTextPane.setFocusable(false);
        outputTextPane.setPreferredSize(new Dimension(360, 240));
        JScrollPane scrollPane = new JScrollPane(outputTextPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        // instalación de componentes
        getContentPane().add(scrollPane);
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
        try {
            LauncherFrame frame = new LauncherFrame();
            BootstrapWorker worker = new BootstrapWorker(frame.getOutputTextPane());
            worker.addPropertyChangeListener(frame);
            frame.addWindowListener(worker);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (RuntimeException e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
    }

    protected JTextPane getOutputTextPane() {
        return outputTextPane;
    }

    protected JProgressBar getProgressBar() {
        return progressBar;
    }

}
