package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class RetrievingTheme extends CheckPoint<String> {

    private final Window window;

    public RetrievingTheme(Window window) {
        this.window = window;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            String classTheme = App.settings.getTheme();
            SwingUtilities.invokeAndWait(() -> {
                try {
                    if (!UIManager.getLookAndFeel().getClass().getName().equals(classTheme)) {
                        UIManager.setLookAndFeel(classTheme);
                        SwingUtilities.updateComponentTreeUI(window);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return classTheme;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
