package ar.com.elbaden.main;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;

public class App implements Runnable {

    public static Settings settings;

    public static String LOCALES_DIR = "locales/strings";

    private App() {
        settings = new Settings();
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(new App());
        } catch (RuntimeException e) {
            System.exit(1);
        }
    }

    @Override
    public void run() {
        LoadingScreen.createAndShow();
    }

}
