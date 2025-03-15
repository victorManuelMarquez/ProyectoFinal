package ar.com.elbaden.main;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
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
        try {
            String pattern = "%t" + File.separator + "java_log.txt";
            FileHandler fileHandler = new FileHandler(pattern, false);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            return Optional.of(fileHandler);
        } catch (IOException e) {
            LOGGER.severe(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

}
