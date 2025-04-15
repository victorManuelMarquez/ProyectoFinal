package ar.com.elbaden.background.task;

import ar.com.elbaden.exception.WorkspaceNotFoundException;
import ar.com.elbaden.main.Settings;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

public class CheckWorkspaceDir implements Callable<String> {

    @Override
    public String call() throws Exception {
        Optional<File> applicationFolder = Settings.getAppDir();
        if (applicationFolder.isPresent()) {
            if (applicationFolder.get().exists()) {
                return applicationFolder.get().getPath();
            } else {
                throw new WorkspaceNotFoundException("el directorio no existe.");
            }
        } else {
            throw new Exception("fatal");
        }
    }

}
