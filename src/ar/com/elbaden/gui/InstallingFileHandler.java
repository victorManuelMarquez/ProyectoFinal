package ar.com.elbaden.gui;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class InstallingFileHandler extends CheckPoint<FileHandler> {

    private static final Logger LOGGER = Logger.getLogger(InstallingFileHandler.class.getName());
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
            FileHandler fileHandler = new FileHandler(logFile.getPath(), false);
            LOGGER.addHandler(fileHandler);
            return fileHandler;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
