package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class CheckingXMLFile extends CheckPoint<String> {

    private final File xmlFile;
    private final String xmlFileFound;

    public CheckingXMLFile(File xmlFile) {
        this.xmlFile = xmlFile;
        // localización
        xmlFileFound = App.MESSAGES.getString("f.checking.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (xmlFile.exists()) {
                return MessageFormat.format(xmlFileFound, xmlFile.getName());
            } else {
                throw new FileNotFoundException(xmlFile.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
