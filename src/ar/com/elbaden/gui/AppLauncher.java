package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppLauncher extends SwingWorker<Void, String> implements ActionListener {

    private final Timer countdown;
    private final int totalSeconds = 11;
    private int seconds = totalSeconds;

    public AppLauncher() {
        countdown = new Timer(1000, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int old = seconds;
        seconds--;
        firePropertyChange("progress",
                calculateProgress(old, totalSeconds),
                calculateProgress(seconds, totalSeconds));
        firePropertyChange("countdown", countdownMessage(old), countdownMessage(seconds));
        if (seconds == 0) {
            countdown.stop();
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        File appFolder = new File(System.getProperty("user.home"), App.FOLDER);
        int progressValue = 0;
        try {
            List<CheckPoint<?>> checkPoints = List.of(new CheckingAppFolder(appFolder));
            processCheckPoint(checkPoints, progressValue);
        } catch (Exception e) {
            List<CheckPoint<?>> checkPoints = List.of(new RestoringAppFolder(appFolder));
            processCheckPoint(checkPoints, progressValue);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(System.out::println);
    }

    @Override
    protected void done() {
        try {
            Void ignore = get();
            System.out.println("Finalizado.");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        countdown.start();
    }

    private int calculateProgress(int value, int total) {
        return value * 100 / total;
    }

    private String countdownMessage(int second) {
        return String.format("Terminado, saliendo en %d segundos...", second);
    }

    private void processCheckPoint(List<CheckPoint<?>> checkPoints, int progress)
            throws ExecutionException, InterruptedException {
        int total = progress + checkPoints.size();
        int value = progress;
        for (CheckPoint<?> checkPoint : checkPoints) {
            try (ExecutorService service = Executors.newSingleThreadExecutor()) {
                Future<?> future = service.submit(checkPoint);
                try {
                    publish(future.get().toString());
                    value++;
                    setProgress(calculateProgress(value, total));
                } catch (InterruptedException | ExecutionException e) {
                    publish(e.getMessage());
                    service.shutdownNow();
                    throw e;
                }
            }
        }
    }

}
