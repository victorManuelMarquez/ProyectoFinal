package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ApplyingTheme extends CheckPoint<String> {

    private final Window window;

    public ApplyingTheme(Window window) {
        this.window = window;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            String classTheme = (String) App.defaults().get(Settings.THEME_KEY);
            SwingUtilities.invokeLater(() -> {
                try {
                    LookAndFeel actualTheme = UIManager.getLookAndFeel();
                    if (!actualTheme.getClass().getName().equals(classTheme)) {
                        UIManager.setLookAndFeel(classTheme);
                        SwingUtilities.updateComponentTreeUI(window);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);
                }
            });
            return classTheme;
        } catch (RuntimeException e) {
            throw new ExecutionException(e);
        }
    }

}
