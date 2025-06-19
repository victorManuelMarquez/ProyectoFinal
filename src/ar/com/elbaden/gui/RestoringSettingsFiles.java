package ar.com.elbaden.gui;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class RestoringSettingsFiles extends CheckPoint {

    public RestoringSettingsFiles(ResourceBundle messages, Object... files) {
        super(messages, files);
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            String pattern = getMessages().getString("restoringFile.format");
            StringBuilder builder = new StringBuilder();
            for (Object value : getValues()) {
                if (value instanceof File file) {
                    switch (file.getName()) {
                        case Settings.XSD_FILE_NAME -> settings.restoreXSD(file, 4);
                        case Settings.XSL_FILE_NAME -> settings.restoreXSL(file, 2);
                        case Settings.XML_FILE_NAME -> settings.restoreXML(file, 4);
                    }
                    String message = MessageFormat.format(pattern, file.getName());
                    builder.append(message).append(System.lineSeparator());
                }
            }
            getRecord().setLevel(Level.INFO);
            getRecord().setMessage(builder.toString());
            return builder.toString();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
