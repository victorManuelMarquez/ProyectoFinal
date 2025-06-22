package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

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
                String pattern = App.messages.getString("directoryFound");
                return buildMessages(Level.FINE, pattern, file);
            } else if (file.mkdir()) {
                String pattern = App.messages.getString("directoryCreated");
                return buildMessages(Level.INFO, pattern, file);
            } else {
                String pattern = App.messages.getString("directoryCannotCreated");
                throw new IOException(buildSimpleMessage(pattern, file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
