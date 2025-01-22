package ar.com.elbaden.task;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

public final class AppChecker extends SwingWorker<Void, String> implements PropertyChangeListener {

    private final JTextArea publisher;
    private final Window root;
    private final List<Thread> tasks;
    private ResourceBundle messages;
    private final Cursor defaultCursor;
    private boolean dumpProperties;

    public AppChecker(JTextArea publisher) {
        this.publisher = publisher;
        root = SwingUtilities.windowForComponent(publisher);
        tasks = Arrays.asList(
                new Thread(this::tryLoadSettings),
                new Thread(this::trySaveSettings),
                new Thread(this::initiateMySQLDriver)
        );
        try {
            setMessages(ResourceBundle.getBundle(App.LOCALES_DIR));
        } catch (MissingResourceException e) {
            // en teoría esto no debería pasar
            System.exit(1);
        }
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
            Class<?> clazz = Class.forName("com.cj.jdbc.Driver");
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
