package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class LoadingSettings extends CheckPoint {

    public LoadingSettings(ResourceBundle messages, Object... files) {
        super(messages, files);
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            String pattern = getMessages().getString("propertiesLoad.formatChoice");
            for (Object value : getValues()) {
                if (value instanceof File file) {
                    settings.loadDocument(file);
                }
            }
            Map<String, Object> values = settings.mapAll();
            values.forEach(App::setDefault);
            String message = MessageFormat.format(pattern, App.getProperties().size());
            getRecord().setLevel(Level.FINE);
            getRecord().setMessage(message);
            return message;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
