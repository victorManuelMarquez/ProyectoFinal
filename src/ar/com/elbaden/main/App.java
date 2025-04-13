package ar.com.elbaden.main;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class App {

    static public final String FOLDER_NAME = ".baden";
    static public final String LANG = "localization/strings";

    static private final Logger GLOBAL_LOGGER = Logger.getGlobal();

    private App() {
        GLOBAL_LOGGER.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        App app = new App();
        Optional<FileHandler> fileHandler = app.getFileHandler();
        fileHandler.ifPresent(GLOBAL_LOGGER::addHandler);
        if (fileHandler.isEmpty()) {
            System.exit(1);
        }
        try {
            ResourceBundle messages = ResourceBundle.getBundle(LANG);
            GLOBAL_LOGGER.info(messages.getString("log.info.app.start"));
            GLOBAL_LOGGER.info(messages.getString("log.info.app.finished"));
        } catch (MissingResourceException resourceException) {
            GLOBAL_LOGGER.severe(resourceException.getLocalizedMessage());
            System.exit(1);
        } catch (RuntimeException exception) {
            GLOBAL_LOGGER.warning(exception.getLocalizedMessage());
        }
    }

    public Optional<FileHandler> getFileHandler() {
        try {
            File logDir = createLogDir();
            if (logDir.exists()) {
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                File logFile = new File(logDir, "log.txt");
                FileHandler fileHandler = new FileHandler(logFile.getPath(), false);
                fileHandler.setFormatter(simpleFormatter);
                fileHandler.setLevel(Level.ALL);
                return Optional.of(fileHandler);
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace(System.err);
            String title = e.getClass().getSimpleName();
            String message = e.getLocalizedMessage();
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }
        return Optional.empty();
    }

    public File createLogDir() {
        File tempDir = Settings.getTempDir();
        if (tempDir.exists()) {
            return tempDir;
        } else {
            if (tempDir.mkdir()) {
                return tempDir;
            } else {
                return null;
            }
        }
    }

}
