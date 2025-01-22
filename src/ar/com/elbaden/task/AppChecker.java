package ar.com.elbaden.task;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class AppChecker extends SwingWorker<Void, String> {

    private final JTextArea publisher;
    private final Window root;

    public AppChecker(JTextArea publisher) {
        this.publisher = publisher;
        root = SwingUtilities.windowForComponent(publisher);
    }

    @Override
    protected Void doInBackground() {
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
        if (!isCancelled()) {
            getRoot().dispose();
        } else {
            firePropertyChange("countdown", false, true);
        }
    }

    JTextArea getPublisher() {
        return publisher;
    }

    public Window getRoot() {
        return root;
    }

}
