package ar.com.elbaden.main;

import ar.com.elbaden.background.BootstrapWorker;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App implements Runnable {

    public static final String FOLDER_NAME = ".baden";
    public static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/strings";

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public App() {
        GLOBAL_LOGGER.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        BootstrapWorker worker = new BootstrapWorker();
        worker.execute();
    }

}
