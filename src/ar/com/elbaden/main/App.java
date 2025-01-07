package ar.com.elbaden.main;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.util.Properties;

public class App implements Runnable {

    public static Properties properties;

    private App() {
        properties = new Properties();
        properties.putAll(Settings.getDefaults());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        LoadingScreen.createAndShow();
    }

}
