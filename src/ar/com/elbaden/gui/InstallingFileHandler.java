package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class InstallingFileHandler extends CheckPoint<FileHandler> {

    private final File outputDir;
    private final LogRecord record;

    public InstallingFileHandler(File outputDir, LogRecord record) {
        this.outputDir = outputDir;
        this.record = record;
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
            fileHandler.publish(record);
            App.LOGGER.addHandler(fileHandler);
            return fileHandler;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
