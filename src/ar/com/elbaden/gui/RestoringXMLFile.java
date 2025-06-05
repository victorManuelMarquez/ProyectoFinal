package ar.com.elbaden.gui;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class RestoringXMLFile extends CheckPoint<String> {

    private final File xmlFile;

    public RestoringXMLFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = new Settings();
            SwingUtilities.invokeLater(() -> {
                try {
                    settings.restoreXML(xmlFile);
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            });
            return xmlFile.getName();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
