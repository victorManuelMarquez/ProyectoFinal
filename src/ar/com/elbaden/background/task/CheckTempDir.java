package ar.com.elbaden.background.task;

import ar.com.elbaden.main.Settings;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CheckTempDir implements Callable<String> {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException("revisión del directorio temporal [cancelado]");
        }
        try {
            File tempDir = new File(Settings.temporalDirPath());
            if (tempDir.exists()) {
                return "[ ✓ ] directorio temporal";
            } else {
                return (tempDir.mkdir() ? "[ ✓ ]" : "[ ✗ ]") + " mkdir → " + tempDir;
            }
        } catch (RuntimeException e) {
            throw new ExecutionException(e);
        }
    }

}
