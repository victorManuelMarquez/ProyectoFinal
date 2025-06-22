package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class CheckingFile extends CheckPoint {

    private final File file;

    public CheckingFile(File file) {
        this.file = file;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (file.exists()) {
                String pattern = App.messages.getString("fileFound");
                return buildMessages(Level.FINEST, pattern, file);
            } else {
                String pattern = App.messages.getString("fileNotFound");
                throw new FileNotFoundException(buildSimpleMessage(pattern, file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
