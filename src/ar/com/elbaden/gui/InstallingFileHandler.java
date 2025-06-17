package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

public class InstallingFileHandler extends CheckPoint<String> {

    private final File outputDir;
    private final String fileHandlerInstalled;

    public InstallingFileHandler(File outputDir) {
        this.outputDir = outputDir;
        // localización
        fileHandlerInstalled = App.MESSAGES.getString("f.fileHandlerInstalled");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            FileHandler fileHandler = fileHandlerExists();
            if (fileHandler != null) {
                return MessageFormat.format(fileHandlerInstalled, fileHandler);
            }
            File logFile = new File(outputDir, "log.txt");
            fileHandler = new TxtFileHandler(logFile);
            App.LOGGER.addHandler(fileHandler);
            return MessageFormat.format(fileHandlerInstalled, fileHandler);
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

        private final File outputFile;

        public TxtFileHandler(File outputFile) throws IOException, SecurityException {
            super(outputFile.getPath(), false);
            this.outputFile = outputFile;
            setFormatter(new SimpleFormatter());
        }

        @Override
        public String toString() {
            return outputFile.getName();
        }

    }

}
