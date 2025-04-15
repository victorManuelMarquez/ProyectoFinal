package ar.com.elbaden.main;

import java.io.File;

public class Settings {

    public static String temporalDirPath() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir != null) {
            return tmpDir + File.separator + App.FOLDER_NAME;
        } else throw new IllegalStateException("entorno no soportado"); // en teoría que pase esto es casi imposible
    }

}
