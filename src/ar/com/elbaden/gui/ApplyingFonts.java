package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ApplyingFonts extends CheckPoint {

    private final Window window;

    public ApplyingFonts(ResourceBundle messages, Window window) {
        super(messages);
        this.window = window;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            int total = 0;
            total = Settings.applyFont(App.getProperties(), window, total);
            String pattern = getMessages().getString("applyingFonts.formatChoice");
            String message = MessageFormat.format(pattern, total);
            getRecord().setLevel(Level.INFO);
            getRecord().setMessage(message);
            getRecord().setParameters(new Object[] { total });
            return message;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
