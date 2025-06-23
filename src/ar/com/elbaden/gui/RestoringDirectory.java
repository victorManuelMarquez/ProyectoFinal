package ar.com.elbaden.gui;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class RestoringDirectory extends CheckPoint {

    private final File file;

    public RestoringDirectory(File file) {
        this.file = file;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (file.exists()) {
                return buildMessage(Level.FINE, "directoryFound", file);
            } else if (file.mkdir()) {
                return buildMessage(Level.INFO, "directoryCreated", file);
            } else {
                throw new IOException(buildPublicMessage("directoryCannotCreated", file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
