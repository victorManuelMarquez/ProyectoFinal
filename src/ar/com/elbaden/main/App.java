package ar.com.elbaden.main;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.*;

public class App implements Runnable {

    public static Settings settings;

    public static String LOCALES_DIR = "locales/strings";

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private final ResourceBundle bundle;

    private App() {
        settings = new Settings();
        bundle = ResourceBundle.getBundle(LOCALES_DIR);
        try {
            // Este handler debe estar disponible para las demás clases
            String pattern = "%t" + File.separator + "java_log.txt";
            FileHandler txtHandler = new FileHandler(pattern, false);
            txtHandler.setFormatter(new SimpleFormatter());
            txtHandler.setLevel(Level.ALL);
            LOGGER.addHandler(txtHandler); // agregar en otros loggers
            LOGGER.setLevel(Level.INFO);
            LOGGER.info("prueba...");
        } catch (IOException e) {
            LOGGER.severe(bundle.getString("log.cannotSetFileHandler"));
        }
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(new App());
        } catch (RuntimeException e) {
            LOGGER.severe(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        LOGGER.info(bundle.getString("log.startingApp"));
        LoadingScreen.createAndShow();
    }

}
