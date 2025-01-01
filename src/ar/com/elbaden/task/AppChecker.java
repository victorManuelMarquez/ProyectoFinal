package ar.com.elbaden.task;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class AppChecker extends SwingWorker<Void, String> {

    private final JTextArea publisher;

    public AppChecker(JTextArea publisher) {
        this.publisher = publisher;
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        String localStarting = "Iniciando comprobación...";
        publish(localStarting);
        Thread.sleep(3000);
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        String chunk = chunks.getLast();
        getPublisher().append(chunk + System.lineSeparator());
    }

    @Override
    protected void done() {
        try {
            Void ignore = get();
        } catch (InterruptedException | ExecutionException e) {
            // ignore
        }
        if (!isCancelled()) {
            String localFinished = "Comprobación finalizada.";
            publish(localFinished);
            firePropertyChange("everythingIsOk", "checking", "done");
        }
    }

    public JTextArea getPublisher() {
        return publisher;
    }

}
