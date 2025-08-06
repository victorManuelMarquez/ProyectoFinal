package ar.com.elbaden.main;

import ar.com.elbaden.gui.LauncherFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static final String WORKSPACE_FOLDER = ".baden";
    public static ResourceBundle messages;

    static {
        // cargo el archivo de localización
        messages = ResourceBundle.getBundle("i18n/messages");
        String userHomePath = System.getProperty("user.home");
        boolean consoleOnly = userHomePath == null;
        File logFile = null;
        if (!consoleOnly) {
            File appDir = new File(userHomePath, WORKSPACE_FOLDER);
            consoleOnly = !appDir.exists() && !appDir.mkdir();
            if (!consoleOnly) {
                logFile = new File(appDir, "log.txt");
            }
        }
        if (logFile != null) {
            try {
                FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                LOGGER.addHandler(fileHandler);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(LauncherFrame::createAndShow);
    }

}
