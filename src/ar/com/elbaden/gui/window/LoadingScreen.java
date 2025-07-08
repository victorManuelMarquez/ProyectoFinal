package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.Launcher;
import ar.com.elbaden.gui.component.DisplayPane;
import ar.com.elbaden.gui.component.InfoProgressBar;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

public class LoadingScreen extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(LoadingScreen.class.getName());

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private LoadingScreen(String title) throws HeadlessException {
        super(title);

        // componentes
        DisplayPane displayPane = new DisplayPane(8, 24);
        JScrollPane scrollPane = new JScrollPane(displayPane);
        InfoProgressBar progressBar = new InfoProgressBar();

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
                launcher.stop();
            }
        });
    }

    private Launcher createLauncher(DisplayPane displayPane, InfoProgressBar progressBar) {
        Launcher launcher = new Launcher(displayPane);
        launcher.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof Integer integer) {
                    progressBar.setValue(integer);
                }
            } else if ("countdown".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof String value) {
                    progressBar.setShowingError(true);
                    progressBar.setString(value);
                }
            }
        });
        return launcher;
    }

    public static void createAndShow() {
        try {
            LoadingScreen loadingScreen = new LoadingScreen(App.messages.getString("loadingScreen.title"));
            loadingScreen.pack();
            loadingScreen.setLocationRelativeTo(null);
            loadingScreen.setVisible(true);
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
