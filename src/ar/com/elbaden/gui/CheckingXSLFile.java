package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class CheckingXSLFile extends CheckPoint<String> {

    private final File xslFile;
    private final String xslFileFound;

    public CheckingXSLFile(File xslFile) {
        this.xslFile = xslFile;
        // localización
        xslFileFound = App.MESSAGES.getString("f.checking.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (xslFile.exists()) {
                return MessageFormat.format(xslFileFound, xslFile.getName());
            } else {
                throw new FileNotFoundException(xslFile.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
