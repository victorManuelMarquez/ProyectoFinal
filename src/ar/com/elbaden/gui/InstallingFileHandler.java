package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class InstallingFileHandler extends CheckPoint<FileHandler> {

    private final File outputDir;

    public InstallingFileHandler(File outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public FileHandler call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            File logFile = new File(outputDir, "log.txt");
            SimpleFormatter formatter = new SimpleFormatter();
            FileHandler fileHandler = new FileHandler(logFile.getPath(), true);
            fileHandler.setFormatter(formatter);
            App.LOGGER.addHandler(fileHandler);
            return fileHandler;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
