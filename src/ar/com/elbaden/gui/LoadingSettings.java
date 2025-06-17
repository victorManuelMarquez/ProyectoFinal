package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class LoadingSettings extends CheckPoint<String> {

    private final File xsdFile;
    private final File xslFile;
    private final File xmlFile;
    private final String totalLoaded;

    public LoadingSettings(File xsdFile, File xslFile, File xmlFile) {
        this.xsdFile = xsdFile;
        this.xslFile = xslFile;
        this.xmlFile = xmlFile;
        totalLoaded = App.MESSAGES.getString("f.totalSettingsLoaded");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.loadDocument(xsdFile, xslFile, xmlFile);
            settings.mapAll().forEach(App::putDefault);
            return MessageFormat.format(totalLoaded, App.defaults().size());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
