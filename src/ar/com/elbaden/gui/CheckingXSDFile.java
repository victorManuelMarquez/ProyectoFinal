package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

public class CheckingXSDFile extends CheckPoint<File> {

    private final File xsdFile;

    public CheckingXSDFile(File xsdFile) {
        this.xsdFile = xsdFile;
    }

    @Override
    public File call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (xsdFile.exists()) {
                return xsdFile;
            } else {
                throw new FileNotFoundException(xsdFile.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
