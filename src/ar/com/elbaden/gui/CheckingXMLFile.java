package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

public class CheckingXMLFile extends CheckPoint<File> {

    private final File xmlFile;

    public CheckingXMLFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public File call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (xmlFile.exists()) {
                return xmlFile;
            } else {
                throw new FileNotFoundException(xmlFile.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
