package ar.com.elbaden.main;

import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static final String BUNDLE_NAME = "i18n/messages";
    public static final String FOLDER_NAME = ".baden";
    public static final String LOG_FILE_NAME = "log.txt";

    private static Map<String, Object> properties;

    static {
        try {
            ResourceBundle messages;
            messages = ResourceBundle.getBundle(BUNDLE_NAME);
            LOGGER.setResourceBundle(messages);
            File appFolder = new File(System.getProperty("user.home"), FOLDER_NAME);
            if (!appFolder.exists()) {
                boolean ignore = appFolder.mkdir();
            }
            File logFile = new File(appFolder, LOG_FILE_NAME);
            try {
                FileHandler fileHandler = new FileHandler(logFile.getPath());
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                LOGGER.addHandler(fileHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            properties = new HashMap<>();
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(LoadingScreen::createAndShow);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public static void setDefault(String key, Object value) {
        properties.put(key, value);
    }

}
