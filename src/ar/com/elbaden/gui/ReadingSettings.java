package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ReadingSettings extends CheckPoint {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.loadXML(Settings.getXMLFile());
            App.properties.putAll(settings.collectNodes());
            App.fontMap.putAll(settings.collectFonts());
            return buildMessage(Level.FINEST, "settingsLoadedSuccessfully");
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
