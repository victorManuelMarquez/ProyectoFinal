package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
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
            FileHandler fileHandler = fileHandlerExists();
            if (fileHandler != null) {
                return fileHandler;
            }
            File logFile = new File(outputDir, "log.txt");
            fileHandler = new TxtFileHandler(logFile.getPath());
            App.LOGGER.addHandler(fileHandler);
            return fileHandler;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    private FileHandler fileHandlerExists() {
        FileHandler found = null;
        for (Handler handler : App.LOGGER.getHandlers()) {
            if (handler instanceof TxtFileHandler txtFileHandler) {
                found = txtFileHandler;
                break;
            }
        }
        return found;
    }

    static class TxtFileHandler extends FileHandler {

        public TxtFileHandler(String pattern) throws IOException, SecurityException {
            super(pattern, false);
            setFormatter(new SimpleFormatter());
        }

    }

}
