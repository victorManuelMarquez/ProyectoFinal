package ar.com.elbaden.gui;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RestoringAppFolder extends CheckPoint<String> {

    private final File appFolder;

    public RestoringAppFolder(File appFolder) {
        this.appFolder = appFolder;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (appFolder.mkdir()) {
                return appFolder.getPath();
            } else {
                if (appFolder.exists()) {
                    return appFolder.getName();
                } else {
                    throw new IOException(appFolder.getPath());
                }
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
