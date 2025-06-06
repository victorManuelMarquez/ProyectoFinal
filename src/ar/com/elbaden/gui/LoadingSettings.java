package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class LoadingSettings extends CheckPoint<String> {

    private final File xsdFile;
    private final File xmlFile;

    public LoadingSettings(File xsdFile, File xmlFile) {
        this.xsdFile = xsdFile;
        this.xmlFile = xmlFile;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.loadDocument(xsdFile, xmlFile);
            App.putDefault(Settings.THEME_KEY, settings.getTheme());
            settings.getFontsMap().forEach(App::putDefault);
            return String.format("%d propiedades cargadas.", App.defaults().size());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
