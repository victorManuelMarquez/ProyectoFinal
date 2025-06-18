package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoadingScreen extends JFrame {

    private LoadingScreen(String title) throws HeadlessException {
        super(title);
        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        JTextArea textArea = new JTextArea(12, 32);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(scrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        // creo el lanzador
        AppLauncher launcher = createLauncher(textArea, progressBar);

        // eventos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                launcher.execute();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                launcher.cancel(true);
                if (launcher.getCountdown().isRunning()) {
                    launcher.getCountdown().stop();
                }
            }
        });
    }

    private AppLauncher createLauncher(JTextArea textArea, JProgressBar progressBar) {
        AppLauncher launcher = new AppLauncher(textArea);
        launcher.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                Integer value = (Integer) evt.getNewValue();
                progressBar.setValue(value);
            } else if ("countdown".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof String value) {
                    progressBar.setString(value);
                }
            }
        });
        return launcher;
    }

    public static void createAndShow() {
        LoadingScreen frame = new LoadingScreen(App.MESSAGES.getString("loadingScreen.title"));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
