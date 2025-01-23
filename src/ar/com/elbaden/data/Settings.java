package ar.com.elbaden.data;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public final class Settings {

    public static final String APP_DIR = ".baden";
    public static final String INI_FILE = "app.ini";

    public static final String KEY_URL_CONNECT = "database.url";
    public static final String KEY_USERNAME_DB = "database.user";
    public static final String KEY_PASSWORD_DB = "database.pass";

    public static final String KEY_CONFIRM_EXIT = "application.confirmExit";

    private final Properties properties;

    public Settings() {
        properties = new Properties();
        properties.putAll(getDefaults());
    }

    public Map<String, String> getDefaults() {
        return Map.ofEntries(
                Map.entry(KEY_URL_CONNECT, "jdbc:mysql://localhost:3306"),
                Map.entry(KEY_USERNAME_DB, "root"),
                Map.entry(KEY_PASSWORD_DB, ""),
                Map.entry(KEY_CONFIRM_EXIT, Boolean.toString(false))
        );
    }

    String getPath() {
        String path = System.getProperty("user.home");
        if (path == null) {
            URL location = Settings.class.getProtectionDomain().getCodeSource().getLocation();
            try {
                File sourceFile = new File(location.toURI());
                path = sourceFile.getParentFile().getPath();
            } catch (URISyntaxException e) {
                // ignore
            }
        }
        return path;
    }

    public boolean loadIni() throws IOException {
        String parentDir = getPath();
        if (parentDir == null) return false;
        parentDir += File.separator + APP_DIR;
        File iniFile = new File(parentDir, INI_FILE);
        try (
                FileInputStream inputStream = new FileInputStream(iniFile);
                InputStreamReader reader = new InputStreamReader(inputStream)
        ) {
            getProperties().load(reader);
        }
        return true;
    }

    public boolean saveIni(String comments) throws IOException {
        String parentDir = getPath();
        if (parentDir == null) return false;
        parentDir += File.separator + APP_DIR;
        File iniFile = new File(parentDir, INI_FILE);
        try (
                FileOutputStream outputStream = new FileOutputStream(iniFile);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream)
        ) {
            getProperties().store(writer, comments);
        }
        return true;
    }

    public Properties getProperties() {
        return properties;
    }

}
