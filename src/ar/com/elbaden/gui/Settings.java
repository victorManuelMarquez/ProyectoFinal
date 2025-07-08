package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Settings extends Properties {

    public static final String APP_FOLDER = ".baden";
    public static final String BASE_KEY = "settings";

    public Settings() {
        loadDefaults();
    }

    public File getAppFolder() {
        return new File(System.getProperty("user.home"), APP_FOLDER);
    }

    public File getLog() {
        return new File(getAppFolder(), "log.txt");
    }

    public File getPropertiesFile() {
        return new File(getAppFolder(), BASE_KEY + ".properties");
    }

    public Map<String, String> defaults() {
        LookAndFeel laf = UIManager.getLookAndFeel();
        Map<String, String> values = new HashMap<>();
        if ("Metal".equals(laf.getID())) {
            String metalKey = "swing.boldMetal";
            boolean boldMetal = !laf.getDefaults().containsKey(metalKey) || UIManager.getBoolean(metalKey);
            values.put(createKey(metalKey), Boolean.toString(boldMetal));
        }
        String lafKey = "lookAndFeel";
        values.put(createKey("showClosingDialog"), Boolean.toString(true));
        values.put(createKey(lafKey).concat(".className"), laf.getClass().getName());
        values.put(createKey(lafKey).concat(".id"), laf.getID());
        return Collections.unmodifiableMap(values);
    }

    private String createKey(String key) {
        return BASE_KEY + "." + key;
    }

    public void loadDefaults() {
        if (defaults == null) {
            defaults = new Properties();
        }
        defaults.putAll(defaults());
        putAll(defaults);
    }

    public String load() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getPropertiesFile())) {
            load(inputStream);
            return App.messages.getString("settings.loaded");
        }
    }

    public String save() throws IOException {
        String commentary = App.messages.getString("settings.commentary");
        try (FileOutputStream outputStream = new FileOutputStream(getPropertiesFile())) {
            store(outputStream, commentary);
            return App.messages.getString("settings.saved");
        }
    }

    public int count() {
        return keySet().size();
    }

    public String changeLook(Window origin) throws Exception {
        String id = getProperty("settings.lookAndFeel.id");
        String className = getProperty("settings.lookAndFeel.className");
        if (UIManager.getLookAndFeel().getClass().getName().equals(className)) {
            String pattern = App.messages.getString("alreadyLookAndFeelSet");
            return MessageFormat.format(pattern, id);
        }
        if (SwingUtilities.isEventDispatchThread()) {
            updateLookAndFeel(className, origin);
        } else {
            // actualización segura
            SwingUtilities.invokeAndWait(() -> {
                try {
                    updateLookAndFeel(className, origin);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        String pattern = App.messages.getString("lookAndFeelSet");
        return MessageFormat.format(pattern, id);
    }

    private void updateLookAndFeel(String className, Window origin) throws Exception {
        UIManager.setLookAndFeel(className);
        SwingUtilities.updateComponentTreeUI(origin);
    }

}
