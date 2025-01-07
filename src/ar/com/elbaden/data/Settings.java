package ar.com.elbaden.data;

import ar.com.elbaden.gui.modal.PublishError;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.io.*;
import java.util.Map;

public final class Settings {

    public static final String WORKING_DIR = ".baden";
    public static final String SETTINGS_FILE = "app.ini";

    public static final String KEY_USER_DATABASE = "database.user";
    public static final String KEY_PASSWORD_DATABASE = "database.pass";

    private Settings() {
        // ignore
    }

    public static Map<String, String> getDefaults() {
        return Map.ofEntries(
                Map.entry(KEY_USER_DATABASE, "root"),
                Map.entry(KEY_PASSWORD_DATABASE, "")
        );
    }

    public File getFile() throws FileNotFoundException {
        String userHome = System.getenv("user.home");
        if (userHome == null)
            throw new RuntimeException("user.home == null");
        String path = userHome + File.pathSeparator + WORKING_DIR;
        File settingsFile = new File(path, SETTINGS_FILE);
        if (settingsFile.getParentFile().exists())
            return settingsFile;
        else {
            String localFormattedFileNotFound = "El archivo %s no existe.";
            String message = String.format(localFormattedFileNotFound, settingsFile);
            throw new FileNotFoundException(message);
        }
    }

    public File createFile() throws IOException {
        String userHome = System.getenv("user.home");
        if (userHome == null)
            throw new RuntimeException("user.home == null");
        String path = userHome + File.pathSeparator + WORKING_DIR;
        File settingsFile = new File(path, SETTINGS_FILE);
        if (!settingsFile.getParentFile().exists()) {
            if (!settingsFile.getParentFile().mkdir()) {
                String localFormattedDirCannotCreated = "No se pudo crear el directorio \"%s\"";
                String message = String.format(localFormattedDirCannotCreated, settingsFile.getParentFile());
                throw new IOException(message);
            }
        }
        return settingsFile;
    }

    public static boolean loadExternal(JFrame root) {
        boolean success = false;
        Settings settings = new Settings();
        try {
            File settingsFile = settings.getFile();
            try (
                    FileInputStream inputStream = new FileInputStream(settingsFile);
                    InputStreamReader reader = new InputStreamReader(inputStream)
            ) {
                App.properties.load(reader);
                success = true;
            } catch (IOException ioException) {
                PublishError.createAndShow(root, ioException);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            PublishError.createAndShow(root, fileNotFoundException);
        }
        return success;
    }

    public static boolean storeExternal(JFrame root) {
        boolean success = false;
        Settings settings = new Settings();
        try {
            File newSettingsFile = settings.createFile();
            try (
                    FileOutputStream outputStream = new FileOutputStream(newSettingsFile);
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream)
            ) {
                String localComments = "Configuración por defecto del programa.";
                App.properties.store(writer, localComments);
                success = true;
            }
        } catch (IOException ioException) {
            PublishError.createAndShow(root, ioException);
        }
        return success;
    }

}
