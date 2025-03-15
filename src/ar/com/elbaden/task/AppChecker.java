package ar.com.elbaden.task;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.gui.MainFrame;
import ar.com.elbaden.gui.modal.ConnectionSetUp;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public final class AppChecker extends SwingWorker<Void, String> implements PropertyChangeListener {

    private final JTextArea publisher;
    private final Window root;
    private final List<Thread> tasks;
    private ResourceBundle messages;
    private final Cursor defaultCursor;
    private boolean dumpProperties = true;

    public AppChecker(JTextArea publisher) {
        this.publisher = publisher;
        setMessages(ResourceBundle.getBundle(App.LOCALES_DIR));
        root = SwingUtilities.windowForComponent(publisher);
        tasks = Arrays.asList(
                new Thread(this::tryLoadSettings),
                new Thread(this::trySaveSettings),
                new Thread(this::initiateMySQLDriver),
                new Thread(this::tryConnect)
        );
        defaultCursor = root.getCursor();
        addPropertyChangeListener(this);
    }

    @Override
    protected Void doInBackground() throws Exception {
        getRoot().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        publish(getMessages().getString("message.starting.app") + "...");
        Iterator<Thread> taskIterator = getTasks().iterator();
        int counter = 1, total = getTasks().size();
        while (!isCancelled() && taskIterator.hasNext()) {
            Thread thread = taskIterator.next();
            thread.setDaemon(true); // se puede cancelar antes de terminar
            thread.start();
            thread.join(); // hilo en espera hasta que termine la ejecución
            setProgress(counter * 100 / total);
            counter++;
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            getPublisher().append(chunk + System.lineSeparator());
        }
    }

    @Override
    protected void done() {
        getRoot().setCursor(defaultCursor);
        if (!isCancelled()) {
            getRoot().dispose();
            String localTitle = getMessages().getString("main_frame.title");
            MainFrame.createAndShow(localTitle);
        } else {
            firePropertyChange("countdown", false, true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) && evt.getNewValue() == StateValue.DONE) {
            if (isCancelled()) {
                String localMessage = getMessages().getString("message.task_cancelled");
                publish(localMessage);
            }
        }
    }

    private void tryLoadSettings() {
        publish(getMessages().getString("message.loading.properties") + "...");
        try {
            dumpProperties = !App.settings.loadIni();
            if (!dumpProperties) {
                publish(getMessages().getString("message.properties.load_ok"));
                return;
            }
        } catch (IOException e) {
            String localMessage = getMessages().getString("message.properties.load_failed");
            String message = MessageFormat.format(localMessage, e.getLocalizedMessage());
            publish(message);
        }
        publish(getMessages().getString("message.set_default_properties"));
    }

    private void trySaveSettings() {
        if (!dumpProperties) return;
        publish(getMessages().getString("message.saving.properties") + "...");
        try {
            String comments = getMessages().getString("ini.comments");
            if (App.settings.saveIni(comments)) {
                publish(getMessages().getString("message.properties.save_ok"));
            }
        } catch (IOException e) {
            String localMessage = getMessages().getString("message.properties.save_failed");
            String message = MessageFormat.format(localMessage, e.getLocalizedMessage());
            publish(message);
        }
    }

    private void initiateMySQLDriver() {
        publish(getMessages().getString("message.loading_driver") + "...");
        try {
            Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
            clazz.getConstructor().newInstance();
            String localMessage = getMessages().getString("message.driver_loaded");
            String message = MessageFormat.format(localMessage, clazz.getName());
            publish(message);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            String localMessage = getMessages().getString("message.driver_not_loaded");
            String message = MessageFormat.format(localMessage, e.getLocalizedMessage());
            publish(message);
            cancel(true);
        }
    }

    private void tryConnect() {
        publish(getMessages().getString("message.connecting_database") + "...");
        firePropertyChange("indeterminate", false, true);
        if (DataBank.testConnection(getRoot())) {
            publish(getMessages().getString("message.connection_successfully"));
            firePropertyChange("indeterminate", true, false);
        } else {
            publish(getMessages().getString("message.connection_failed"));
            int tries = 3;
            boolean keepTrying = true;
            do {
                String localSingular = getMessages().getString("message.attempt_remaining");
                String localPlural = getMessages().getString("message.attempts_remaining");
                ChoiceFormat formatter = new ChoiceFormat(new double[]{1, 2}, new String[]{
                        localSingular, localPlural
                });
                publish(MessageFormat.format(formatter.format(tries), tries));
                tries--;
                if (ConnectionSetUp.createAndShow(getRoot())) {
                    publish(getMessages().getString("message.connection_successfully"));
                    keepTrying = false;
                } else {
                    publish(getMessages().getString("message.connection_failed"));
                }
            } while (keepTrying && tries > 0);
            if (keepTrying) {
                cancel(true);
            }
            firePropertyChange("indeterminate", true, false);
        }
    }

    JTextArea getPublisher() {
        return publisher;
    }

    public Window getRoot() {
        return root;
    }

    public List<Thread> getTasks() {
        return tasks;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

}
