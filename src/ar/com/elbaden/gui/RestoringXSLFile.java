package ar.com.elbaden.gui;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class RestoringXSLFile extends CheckPoint<String> {

    private final File xslFile;

    public RestoringXSLFile(File xslFile) {
        this.xslFile = xslFile;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXSL(xslFile);
            return xslFile.getName();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
