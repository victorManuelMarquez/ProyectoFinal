package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

public class CheckingXSLFile extends CheckPoint<File> {

    private final File xslFile;

    public CheckingXSLFile(File xslFile) {
        this.xslFile = xslFile;
    }

    @Override
    public File call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (xslFile.exists()) {
                return xslFile;
            } else {
                throw new FileNotFoundException(xslFile.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
