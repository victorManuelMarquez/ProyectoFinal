package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

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
                return buildMessage("fileFound", file);
            } else {
                throw new FileNotFoundException(buildMessage("fileNotFound", file));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
