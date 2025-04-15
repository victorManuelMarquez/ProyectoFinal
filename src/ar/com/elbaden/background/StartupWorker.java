package ar.com.elbaden.background;

import ar.com.elbaden.background.task.CheckWorkspaceDir;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.logging.Logger;

public final class StartupWorker extends SwingWorker<Void, String> {

    private final ResourceBundle messages;

    static private final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public StartupWorker() {
        messages = ResourceBundle.getBundle(App.LANG);
    }

    @Override
    protected Void doInBackground() {
        List<Callable<String>> tasksList = List.of(new CheckWorkspaceDir());
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            for (Callable<String> task : tasksList) {
                Future<String> result = service.submit(task);
                try {
                    publish(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    service.shutdownNow();
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            System.out.println(chunk);
        }
    }

    @Override
    protected void done() {
        GLOBAL_LOGGER.info(getMessages().getString("log.info.app.finished"));
    }

    public ResourceBundle getMessages() {
        return messages;
    }

}
