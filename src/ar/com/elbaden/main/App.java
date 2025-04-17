package ar.com.elbaden.main;

import ar.com.elbaden.gui.LauncherFrame;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App implements Runnable {

    public static final String FOLDER_NAME = ".baden";
    public static final String MESSAGES = "i18n/messages";

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public App() {
        GLOBAL_LOGGER.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        LauncherFrame.createAndShow();
    }

}
