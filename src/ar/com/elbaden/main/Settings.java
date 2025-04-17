package ar.com.elbaden.main;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Settings {

    public static String temporalDirPath() {
        String property = "java.io.tmpdir";
        String tmpDir = System.getProperty(property);
        if (tmpDir != null) {
            return tmpDir + File.separator + App.FOLDER_NAME;
        } else {
            // en teoría que pase esto es casi imposible
            ResourceBundle messages = ResourceBundle.getBundle(App.MESSAGES);
            String message = MessageFormat.format(messages.getString("system.property.null"), property);
            throw new IllegalStateException(message);
        }
    }

}
