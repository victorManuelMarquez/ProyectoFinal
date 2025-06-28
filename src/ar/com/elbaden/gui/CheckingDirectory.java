package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

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
                return buildMessage("directoryFound", file);
            } else {
                throw new FileNotFoundException(buildMessage("directoryNotFound", file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
