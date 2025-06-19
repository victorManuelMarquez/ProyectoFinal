package ar.com.elbaden.gui;

import ar.com.elbaden.gui.component.DisplayPane;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class LoadingScreen extends JFrame {

    private static ResourceBundle messages;

    private LoadingScreen(String title) throws HeadlessException {
        super(title);
        // componentes
        DisplayPane displayPane = new DisplayPane(8, 24);
        JScrollPane scrollPane = new JScrollPane(displayPane);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(scrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // eventos
        Launcher launcher = createLauncher(displayPane, progressBar);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                launcher.execute();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                launcher.cancel(true);
                launcher.stopCountdown();
            }
        });
    }

    private static Launcher createLauncher(DisplayPane displayPane, JProgressBar progressBar) {
        Launcher launcher = new Launcher(displayPane);
        launcher.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof Integer integer) {
                    progressBar.setValue(integer);
                    String pattern = messages.getString("loadingScreen.loading.format");
                    progressBar.setString(MessageFormat.format(pattern, integer));
                }
            } else if ("countdown".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof Integer integer) {
                    String pattern = messages.getString("loadingScreen.countdown.format");
                    progressBar.setString(MessageFormat.format(pattern, integer));
                }
            }
        });
        return launcher;
    }

    public static void createAndShow() {
        messages = ResourceBundle.getBundle(App.BUNDLE_NAME);
        LoadingScreen loadingScreen = new LoadingScreen(messages.getString("loadingScreen.title"));
        loadingScreen.pack();
        loadingScreen.setLocationRelativeTo(null);
        loadingScreen.setVisible(true);
    }

}
