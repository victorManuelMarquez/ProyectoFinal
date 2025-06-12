package ar.com.elbaden.main;

import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class App {

    public static final String FOLDER = ".baden";
    public static ResourceBundle MESSAGES;
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final Map<String, Object> defaults;

    static {
        try {
            MESSAGES = ResourceBundle.getBundle("i18n/messages");
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
            SwingUtilities.invokeLater(() -> {
                String message = e.getMessage();
                String title = e.getClass().getSimpleName();
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            });
            System.exit(1);
        }
        defaults = new HashMap<>();
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(LoadingScreen::createAndShow);
        } catch (RuntimeException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public static Map<String, Object> defaults() {
        return Collections.unmodifiableMap(defaults);
    }

    public static void putDefault(String key, Object value) {
        defaults.put(key, value);
    }

}
