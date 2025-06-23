package ar.com.elbaden.main;

import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.gui.window.LoadingScreen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static ResourceBundle messages;
    public static Properties properties;
    public static Map<String, Font> fontMap;

    static {
        try {
            properties = new Properties();
            fontMap = new HashMap<>();
            // cargo la localización
            messages = ResourceBundle.getBundle("i18n/messages", Locale.getDefault());
            LOGGER.setResourceBundle(messages);
            // creo el manejador para el archivo log.txt
            File logFile = Settings.getLogFile();
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            FileHandler fileHandler = new FileHandler(logFile.getPath());
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoadingScreen::createAndShow);
    }

}
