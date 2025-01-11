package ar.com.elbaden.task;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.MainFrame;
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
                new Thread(this::checkSettings),
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
            MainFrame.createAndShow();
            getRoot().dispose();
        }
    }

    private void launchCountdown() {
        firePropertyChange("countdown", "checking", "interrupt");
        String localEnd = "Programa terminado.";
        publish(localEnd);
    }

    private void checkSettings() {
        String localLoadingSettings = "Cargando la configuración...";
        publish(localLoadingSettings);
        boolean loaded = Settings.loadExternal(getRoot());
        if (loaded) {
            String localFoundSettings = "Configuración cargada...";
            publish(localFoundSettings);
        } else {
            String localFailLoadSettings = "Falló la lectura de la configuración...";
            publish(localFailLoadSettings);
            boolean defaults = Settings.storeExternal(getRoot());
            if (defaults) {
                String localLoadDefaults = "Configuración por defecto cargada...";
                publish(localLoadDefaults);
            }
        }
    }

    private void checkDriver() {
        String localLoadingDriver = "Cargando driver de MySQL...";
        publish(localLoadingDriver);
        if (DataBank.isDriverPresent(getRoot())) {
            String localDriverSuccess = "Driver encontrado...";
            publish(localDriverSuccess);
        } else {
            String localDriverNotFound = "Driver no encontrado.";
            publish(localDriverNotFound);
            cancel(true);
            launchCountdown();
        }
    }

    private void checkConnection() {
        String localConnecting = "Conectando...";
        publish(localConnecting);
        String set_indeterminate = "progressIndeterminate";
        firePropertyChange(set_indeterminate, false, true);
        String localConnected = "Conexión exitosa...";
        if (DataBank.canConnect(getRoot())) {
            publish(localConnected);
        } else {
            int total = 3, tries = 0;
            do {
                if (ConnectionSetUp.createAndShow(getRoot())) {
                    publish(localConnected);
                    tries = 0;
                } else {
                    ++tries;
                    String localNotConnected = "Conexión fallida.";
                    publish(localNotConnected);
                    String localRetries = "Reintentando...";
                    publish(String.format(localRetries + "\t%d/%d", tries, total));
                }
            } while (tries > 0 && tries != total);
            if (tries == total) {
                cancel(true);
                launchCountdown();
            }
        }
        firePropertyChange(set_indeterminate, true, false);
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
