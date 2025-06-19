package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ApplyingTheme extends CheckPoint {

    private final Window window;

    public ApplyingTheme(ResourceBundle messages, Window window) {
        super(messages);
        this.window = window;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            String className = (String) App.getProperties().get(Settings.BASE_KEY + ".theme");
            String name = Settings.applyTheme(window, className);
            String pattern = getMessages().getString("applyingTheme.format");
            String message = MessageFormat.format(pattern, name);
            getRecord().setLevel(Level.INFO);
            getRecord().setMessage(pattern);
            getRecord().setParameters(new Object[] { className });
            return message;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
