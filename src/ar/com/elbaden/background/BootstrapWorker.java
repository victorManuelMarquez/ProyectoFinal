package ar.com.elbaden.background;

import ar.com.elbaden.background.task.CheckFileHandler;
import ar.com.elbaden.background.task.CheckTempDir;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BootstrapWorker extends SwingWorker<Void, String> {

    private final ResourceBundle messages;

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public BootstrapWorker() {
        messages = ResourceBundle.getBundle(App.RESOURCE_BUNDLE_BASE_NAME);
    }

    @Override
    protected Void doInBackground() {
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            GLOBAL_LOGGER.info(getMessages().getString("log.info.app.start"));
            List<Callable<String>> checkpointsList = new ArrayList<>();
            checkpointsList.add(new CheckTempDir());
            checkpointsList.add(new CheckFileHandler());
            Iterator<Callable<String>> checkpointIterator = checkpointsList.iterator();
            while (!isCancelled() && !service.isShutdown() && checkpointIterator.hasNext()) {
                Callable<String> checkpoint = checkpointIterator.next();
                Future<String> result = service.submit(checkpoint);
                try {
                    publish(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    GLOBAL_LOGGER.severe(e.getLocalizedMessage());
                    service.shutdownNow();
                }
            }
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
            cancel(true);
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
        try {
            GLOBAL_LOGGER.info(getMessages().getString("log.info.app.finished"));
        } catch (RuntimeException e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
    }

    public ResourceBundle getMessages() {
        return messages;
    }

}
