package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class CheckingDirectory extends CheckPoint {

    private final File file;

    public CheckingDirectory(File file) {
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
                return buildMessages(Level.FINEST, pattern, file);
            } else {
                String pattern = App.messages.getString("directoryNotFound");
                throw new FileNotFoundException(buildSimpleMessage(pattern, file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
