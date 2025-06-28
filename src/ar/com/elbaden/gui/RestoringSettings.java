package ar.com.elbaden.gui;

import java.util.concurrent.ExecutionException;

public class RestoringSettings extends CheckPoint {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXML(Settings.getXMLFile(), 4);
            return buildMessage("settingsRestoredSuccessfully");
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
