package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SavingConfirmExit extends CheckPoint<Object> {

    private final File xsdFile;
    private final File xmlFile;
    private final boolean confirm;

    public SavingConfirmExit(File xsdFile, File xmlFile, boolean confirm) {
        this.xsdFile = xsdFile;
        this.xmlFile = xmlFile;
        this.confirm = confirm;
    }

    @Override
    public Object call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.loadDocument(xsdFile, xmlFile);
            settings.setConfirmValue(confirm);
            settings.save(xmlFile);
            App.putDefault(Settings.CONFIRM_EXIT_KEY, Boolean.toString(confirm));
            return App.defaults().get(Settings.CONFIRM_EXIT_KEY);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
