package ar.com.elbaden.gui;

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
                return buildMessage(Level.FINEST, "fileFound", file);
            } else {
                throw new FileNotFoundException(buildPublicMessage("fileNotFound", file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
