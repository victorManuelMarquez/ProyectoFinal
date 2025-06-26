package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.Launcher;
import ar.com.elbaden.gui.component.DisplayPane;
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
        DisplayPane displayPane = new DisplayPane(12, 40);
        JScrollPane scrollPane = new JScrollPane(displayPane);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(scrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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

    private Launcher createLauncher(DisplayPane displayPane, JProgressBar progressBar) {
        Launcher launcher = new Launcher(displayPane);
        launcher.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof Integer integer) {
                    progressBar.setValue(integer);
                }
            } else if ("countdown".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof String value) {
                    progressBar.setString(value);
                }
            }
        });
        return launcher;
    }

    public static void createAndShow() {
        try {
            String title = App.messages.getString("loadingScreen.title");
            LoadingScreen loadingScreen = new LoadingScreen(title);
            loadingScreen.pack();
            loadingScreen.setLocationRelativeTo(null);
            loadingScreen.setVisible(true);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            // registro el evento
            LOGGER.warning(message);
            // muestro gráficamente el error
            String title = e.getClass().getSimpleName();
            int icon = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(null, message, title, icon);
        }
    }

}
