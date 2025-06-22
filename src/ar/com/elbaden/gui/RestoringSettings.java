package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class RestoringSettings extends CheckPoint {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXML(Settings.getXMLFile(), 4);
            String pattern = App.messages.getString("settingsRestoredSuccessfully");
            return buildMessages(Level.INFO, pattern);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
