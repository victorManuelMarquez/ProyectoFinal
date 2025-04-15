package ar.com.elbaden.background.task;

import ar.com.elbaden.main.Settings;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CheckFileHandler implements Callable<String> {

    private final Logger GLOBAL_LOGGER = Logger.getGlobal();

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException("creación del manejador de registro de eventos [cancelado]");
        }
        try {
            File tempDir = new File(Settings.temporalDirPath());
            if (tempDir.exists()) {
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                File logFile = new File(tempDir, "log.txt");
                try {
                    FileHandler fileHandler = new FileHandler(logFile.getPath(), false);
                    fileHandler.setFormatter(simpleFormatter);
                    fileHandler.setLevel(Level.ALL);
                    GLOBAL_LOGGER.addHandler(fileHandler);
                    return "[ ✓ ] registro de eventos";
                } catch (IOException e) {
                    throw new ExecutionException(e);
                }
            } else {
                throw new RuntimeException("el directorio para crear el manejador de eventos no existe.");
            }
        } catch (RuntimeException e) {
            throw new ExecutionException(e);
        }
    }

}
