package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class CheckingAppFolder extends CheckPoint<String> {

    private final File appFolder;
    private final String appFolderExists;

    public CheckingAppFolder(File appFolder) {
        this.appFolder = appFolder;
        // localización
        appFolderExists = App.MESSAGES.getString("f.checking.appFolder");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            if (appFolder.exists()) {
                return MessageFormat.format(appFolderExists, appFolder);
            } else {
                throw new FileNotFoundException(appFolder.getPath());
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
