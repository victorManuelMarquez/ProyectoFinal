package ar.com.elbaden.gui;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class RestoringXSDFile extends CheckPoint<String> {

    private final File xsdFile;

    public RestoringXSDFile(File xsdFile) {
        this.xsdFile = xsdFile;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXSD(xsdFile);
            return xsdFile.getName();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
