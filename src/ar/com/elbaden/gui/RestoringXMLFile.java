package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class RestoringXMLFile extends CheckPoint<String> {

    private final File xmlFile;
    private final String xmlFileRestored;

    public RestoringXMLFile(File xmlFile) {
        this.xmlFile = xmlFile;
        // localización
        xmlFileRestored = App.MESSAGES.getString("f.restoring.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            settings.restoreXML(xmlFile);
            return MessageFormat.format(xmlFileRestored, xmlFile.getName());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
