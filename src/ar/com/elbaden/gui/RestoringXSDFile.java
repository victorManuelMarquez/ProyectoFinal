package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class RestoringXSDFile extends CheckPoint<String> {

    private final File xsdFile;
    private final String xsdFileRestored;

    public RestoringXSDFile(File xsdFile) {
        this.xsdFile = xsdFile;
        // localización
        xsdFileRestored = App.MESSAGES.getString("f.restoring.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXSD(xsdFile);
            return MessageFormat.format(xsdFileRestored, xsdFile.getName());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
