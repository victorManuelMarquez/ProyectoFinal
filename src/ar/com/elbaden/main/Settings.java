package ar.com.elbaden.main;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;

public class Settings {

    static private final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public static File getTempDir() {
        String tmpDirProperty = System.getProperty("java.io.tmpdir");
        String path = tmpDirProperty + File.separator + App.FOLDER_NAME;
        return new File(path);
    }

    public static Optional<File> getAppDir() {
        String userHomeProperty = System.getProperty("user.home");
        if (userHomeProperty == null) {
            URL actualLocation = Settings.class.getProtectionDomain().getCodeSource().getLocation();
            try {
                File actualDir = new File(actualLocation.toURI());
                return Optional.of(actualDir);
            } catch (URISyntaxException e) {
                GLOBAL_LOGGER.severe(e.getLocalizedMessage());
                return Optional.empty();
            }
        }
        String fullPath = userHomeProperty + File.separator + App.FOLDER_NAME;
        return Optional.of(new File(fullPath));
    }

}
