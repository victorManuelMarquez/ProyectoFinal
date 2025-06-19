package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class CheckingFile extends CheckPoint {

    public CheckingFile(ResourceBundle bundle, File file) {
        super(bundle, file);
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            String pattern = getMessages().getString("checkingFile.format");
            // mensaje simple
            StringBuilder builder = new StringBuilder();
            if (getValues()[0] instanceof File file) {
                if (!file.exists()) {
                    throw new FileNotFoundException(file.getPath());
                } else {
                    Settings settings = new Settings();
                    settings.loadDocument(file);
                }
                String message = MessageFormat.format(pattern, file.getName());
                builder.append(message).append(System.lineSeparator());
            }
            // mensaje completo
            getRecord().setLevel(Level.FINE);
            getRecord().setMessage(pattern);
            getRecord().setParameters(getValues());
            return builder.toString(); // retorno el mensaje simple
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
