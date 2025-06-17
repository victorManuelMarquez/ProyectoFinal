package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class CheckingXSDFile extends CheckPoint<String> {

    private final File xsdFile;
    private final String xsdFileFound;

    public CheckingXSDFile(File xsdFile) {
        this.xsdFile = xsdFile;
        // localización
        xsdFileFound = App.MESSAGES.getString("f.checking.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (xsdFile.exists()) {
                return MessageFormat.format(xsdFileFound, xsdFile.getName());
            } else {
                throw new FileNotFoundException(xsdFile.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
