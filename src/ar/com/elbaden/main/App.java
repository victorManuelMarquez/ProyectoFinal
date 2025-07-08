package ar.com.elbaden.main;

import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.gui.window.LoadingScreen;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static ResourceBundle messages;
    public static Settings settings;

    static {
        try {
            settings = new Settings();
            messages = ResourceBundle.getBundle("i18n/messages", Locale.getDefault());
            LOGGER.setResourceBundle(messages);
            FileHandler fileHandler = new FileHandler(settings.getLog().getPath(), false);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException | RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoadingScreen::createAndShow);
    }

}
