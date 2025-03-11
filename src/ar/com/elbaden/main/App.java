package ar.com.elbaden.main;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class App implements Runnable {

    public static Settings settings;

    public static String LOCALES_DIR = "locales/strings";

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private App() {
        settings = new Settings();
    }

    public static void main(String[] args) {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle(LOCALES_DIR);
        } catch (MissingResourceException e) {
            LOGGER.severe(e.getLocalizedMessage());
            System.exit(1);
        }
        try {
            FileHandler txtHandler = new FileHandler("%t/java_log.txt", false);
            txtHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(txtHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            LOGGER.severe(bundle.getString("log.cannotSetFileHandler"));
        }
        try {
            LOGGER.info(bundle.getString("log.startingApp"));
            SwingUtilities.invokeLater(new App());
        } catch (RuntimeException e) {
            LOGGER.severe(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        LoadingScreen.createAndShow();
    }

}
