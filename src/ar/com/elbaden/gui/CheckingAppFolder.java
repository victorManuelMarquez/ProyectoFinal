package ar.com.elbaden.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

public class CheckingAppFolder extends CheckPoint<File> {

    private final File appFolder;

    public CheckingAppFolder(File appFolder) {
        this.appFolder = appFolder;
    }

    @Override
    public File call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (appFolder.exists()) {
                return appFolder;
            } else {
                throw new FileNotFoundException(appFolder.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
