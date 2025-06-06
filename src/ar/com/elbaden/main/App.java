package ar.com.elbaden.main;

import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class App {

    public static final String FOLDER = ".baden";
    private static final Map<String, Object> defaults;

    static {
        defaults = new HashMap<>();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoadingScreen::createAndShow);
    }

    public static Map<String, Object> defaults() {
        return Collections.unmodifiableMap(defaults);
    }

    public static void putDefault(String key, Object value) {
        defaults.put(key, value);
    }

}
