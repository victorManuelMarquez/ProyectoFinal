package ar.com.elbaden.task;

import ar.com.elbaden.connection.DataBank;

import javax.swing.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class AppChecker extends SwingWorker<Void, String> {

    private final JFrame root;
    private final JTextArea publisher;
    private final List<Thread> threads;

    public AppChecker(JFrame root, JTextArea publisher) {
        this.root = root;
        this.publisher = publisher;
        threads = Arrays.asList(
                new Thread(this::checkDriver),
                new Thread(this::checkConnection)
        );
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        String localStarting = "Iniciando comprobación...";
        publish(localStarting);
        int actualThread = 1;
        Iterator<Thread> iterator = threads.iterator();
        while (!isCancelled() && iterator.hasNext()) {
            setProgress(actualThread * 100 / threads.size());
            Thread thread = iterator.next();
            thread.start();
            thread.join();
            actualThread++;
        }
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
        }
    }

    private void launchCountdown() {
        firePropertyChange("countdown", "checking", "interrupt");
    }

    private void checkDriver() {
        if (DataBank.isDriverPresent(getRoot())) {
            String localDriverSuccess = "Driver encontrado...";
            publish(localDriverSuccess);
        } else {
            String localDriverNotFound = "Driver no encontrado.";
            publish(localDriverNotFound);
            launchCountdown();
            cancel(true);
        }
    }

    private void checkConnection() {
        if (DataBank.canConnect(getRoot())) {
            String localConnected = "Conexión exitosa...";
            publish(localConnected);
        } else {
            String localNotConnected = "Conexión fallida.";
            publish(localNotConnected);
        }
    }

    public JFrame getRoot() {
        return root;
    }

    public JTextArea getPublisher() {
        return publisher;
    }

}
