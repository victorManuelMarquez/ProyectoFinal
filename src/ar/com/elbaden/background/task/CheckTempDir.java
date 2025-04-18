package ar.com.elbaden.background.task;

import ar.com.elbaden.main.Settings;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CheckTempDir implements Callable<String> {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException("revisión del directorio temporal");
        }
        try {
            File tempDir = new File(Settings.temporalDirPath());
            if (tempDir.exists()) {
                return "directorio temporal";
            } else {
                if (tempDir.mkdir()) {
                    return "directorio temporal";
                } else throw new IOException("no se pudo crear el directorio temporal");
            }
        } catch (RuntimeException | IOException e) {
            throw new ExecutionException(e);
        }
    }

}
