package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class RestoringAppFolder extends CheckPoint<String> {

    private final File appFolder;
    private final String appFolderCreated;
    private final String appFolderExists;

    public RestoringAppFolder(File appFolder) {
        this.appFolder = appFolder;
        // localización
        appFolderCreated = App.MESSAGES.getString("f.restoring.file");
        appFolderExists = App.MESSAGES.getString("f.checking.file");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (appFolder.mkdir()) {
                return MessageFormat.format(appFolderCreated, appFolder);
            } else {
                if (appFolder.exists()) {
                    return MessageFormat.format(appFolderExists, appFolder);
                } else {
                    throw new IOException(appFolder.getPath());
                }
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
