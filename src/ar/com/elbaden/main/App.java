package ar.com.elbaden.main;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.util.logging.Level.ALL;

public class App implements Runnable {

    public static Settings settings;

    public static String LOCALES_DIR = "locales/strings";

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private final ResourceBundle bundle;

    private App() {
        settings = new Settings();
        bundle = ResourceBundle.getBundle(LOCALES_DIR);
    }

    public static void main(String[] args) {
        Optional<FileHandler> fileHandler = createFileHandler();
        if (fileHandler.isPresent()) {
            LOGGER.addHandler(fileHandler.get());
            LOGGER.setLevel(Level.INFO);
        }
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

    public static Optional<FileHandler> createFileHandler() {
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        try {
            File appFolder = Settings.getAppFolder();
            if (appFolder.exists()) {
                // directorio predefinido del programa
                String pattern = appFolder + File.separator + "log.txt";
                FileHandler fileHandler = new FileHandler(pattern, false);
                fileHandler.setFormatter(simpleFormatter);
                fileHandler.setLevel(ALL);
                return Optional.of(fileHandler);
            }
        } catch (IOException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        try {
            // directorio temporal del sistema operativo
            String pattern = "%t" + File.separator + "java.log";
            FileHandler fileHandler = new FileHandler(pattern, false);
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(ALL);
            return Optional.of(fileHandler);
        } catch (IOException e) {
            LOGGER.severe(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

}
