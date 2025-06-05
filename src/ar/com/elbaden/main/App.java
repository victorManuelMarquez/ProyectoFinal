package ar.com.elbaden.main;

import ar.com.elbaden.gui.LoadingScreen;
import ar.com.elbaden.gui.Settings;

import javax.swing.*;

public class App {

    public static final String FOLDER = ".baden";
    public static Settings settings;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoadingScreen::createAndShow);
    }

}
