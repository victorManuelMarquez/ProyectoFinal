package ar.com.elbaden.task;

import javax.swing.*;
import java.util.List;

public final class Countdown extends SwingWorker<Void, String> {

    private final JFrame root;
    private final JProgressBar publisher;
    private final int seconds;

    public Countdown(JFrame root, JProgressBar publisher, int seconds) {
        this.root = root;
        this.publisher = publisher;
        this.seconds = Math.max(seconds, 10);
        addPropertyChangeListener(evt-> {
            if ("progress".equals(evt.getPropertyName())) {
                publisher.setValue(getProgress());
            }
        });
    }

    public Countdown(JFrame root, JProgressBar publisher) {
        this(root, publisher, 15);
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        String formattedLocalRemaining = "Cerrando en %d segundos...";
        for (int second = getSeconds(); second != 0; second--) {
            setProgress(second * 100 / getSeconds());
            publish(String.format(formattedLocalRemaining, second));
            Thread.sleep(1000);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        String chunk = chunks.getLast();
        getPublisher().setString(chunk);
    }

    @Override
    protected void done() {
        if (!isCancelled())
            getRoot().dispose();
    }

    public JFrame getRoot() {
        return root;
    }

    public JProgressBar getPublisher() {
        return publisher;
    }

    public int getSeconds() {
        return seconds;
    }

}
