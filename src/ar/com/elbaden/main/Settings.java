package ar.com.elbaden.main;

import java.io.File;

public class Settings {

    public static File getTempDir() {
        String tmpDirProperty = System.getProperty("java.io.tmpdir");
        String path = tmpDirProperty + File.separator + App.FOLDER_NAME;
        return new File(path);
    }

}
