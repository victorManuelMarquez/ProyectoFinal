package ar.com.elbaden.background;

import ar.com.elbaden.background.task.CheckFileHandler;
import ar.com.elbaden.background.task.CheckTempDir;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BootstrapWorker extends SwingWorker<Void, String> {

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    @Override
    protected Void doInBackground() {
        GLOBAL_LOGGER.info("Programa iniciado");
        List<Callable<String>> checkpointsList = new ArrayList<>();
        checkpointsList.add(new CheckTempDir());
        checkpointsList.add(() -> {
            throw new InterruptedException("Falla intencional");
        });
        checkpointsList.add(new CheckFileHandler());
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
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
        GLOBAL_LOGGER.info("Programa finalizado");
    }

}
