package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class RestoringXSLFile extends CheckPoint<String> {

    private final File xslFile;
    private final String xslFileRestored;

    public RestoringXSLFile(File xslFile) {
        this.xslFile = xslFile;
        // localización
        xslFileRestored = App.MESSAGES.getString("f.restoring.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXSL(xslFile);
            return MessageFormat.format(xslFileRestored, xslFile.getName());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
