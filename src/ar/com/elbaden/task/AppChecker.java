package ar.com.elbaden.task;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.gui.modal.ConnectionSetUp;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class AppChecker extends SwingWorker<Void, String> {

    private final JFrame root;
    private final JTextArea publisher;
    private final List<Thread> threads;
    private final Cursor defaultCursor;

    public AppChecker(JFrame root, JTextArea publisher) {
        this.root = root;
        this.publisher = publisher;
        threads = Arrays.asList(
                new Thread(this::checkDriver),
                new Thread(this::checkConnection)
        );
        defaultCursor = root.getCursor();
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        Cursor waitingCursor = new Cursor(Cursor.WAIT_CURSOR);
        getRoot().setCursor(waitingCursor);
        String localStarting = "Iniciando comprobación...";
        publish(localStarting);
        int actualThread = 1;
        Iterator<Thread> iterator = getThreads().iterator();
        while (!isCancelled() && iterator.hasNext()) {
            setProgress(actualThread * 100 / getThreads().size());
            Thread thread = iterator.next();
            thread.start();
            thread.join();
            actualThread++;
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(chunk -> getPublisher().append(chunk + System.lineSeparator()));
    }

    @Override
    protected void done() {
        getRoot().setCursor(getDefaultCursor());
        if (!isCancelled()) {
            String localFinished = "Comprobación finalizada.";
            publish(localFinished);
        }
    }

    private void launchCountdown() {
        firePropertyChange("countdown", "checking", "interrupt");
        String localEnd = "Programa terminado.";
        publish(localEnd);
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
        String localConnected = "Conexión exitosa...";
        if (DataBank.canConnect(getRoot())) {
            publish(localConnected);
        } else {
            int total = 3, tries = total;
            do {
                if (ConnectionSetUp.createAndShow(root)) {
                    publish(localConnected);
                    tries = -1;
                } else {
                    String localNotConnected = "Conexión fallida.";
                    publish(localNotConnected);
                    if (tries > 1) {
                        String localRetries = "Reintentando...";
                        publish(String.format(localRetries + "\t%d/%d", tries, total));
                    }
                    tries--;
                }
            } while (tries > 0);
            if (tries == 0) {
                launchCountdown();
                cancel(true);
            }
        }
    }

    public JFrame getRoot() {
        return root;
    }

    public JTextArea getPublisher() {
        return publisher;
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

}
