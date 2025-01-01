package ar.com.elbaden.task;

import ar.com.elbaden.connection.DataBank;

import javax.swing.*;
import java.util.List;

public final class AppChecker extends SwingWorker<Void, String> {

    private final JFrame root;
    private final JTextArea publisher;

    public AppChecker(JFrame root, JTextArea publisher) {
        this.root = root;
        this.publisher = publisher;
    }

    @Override
    protected Void doInBackground() {
        String localStarting = "Iniciando comprobación...";
        publish(localStarting);
        checkDriver();
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        String chunk = chunks.getLast();
        getPublisher().append(chunk + System.lineSeparator());
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            String localFinished = "Comprobación finalizada.";
            publish(localFinished);
            firePropertyChange("everythingIsOk", "checking", "done");
        }
    }

    private void checkDriver() {
        if (DataBank.isDriverPresent(getRoot())) {
            String localDriverSuccess = "Driver encontrado...";
            publish(localDriverSuccess);
        } else {
            String localDriverNotFound = "Driver no encontrado.";
            publish(localDriverNotFound);
            cancel(true);
        }
    }

    public JFrame getRoot() {
        return root;
    }

    public JTextArea getPublisher() {
        return publisher;
    }

}
