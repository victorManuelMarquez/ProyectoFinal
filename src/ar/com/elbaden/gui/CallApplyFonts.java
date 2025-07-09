package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CallApplyFonts implements Callable<String> {

    private final Window master;

    public CallApplyFonts(Window master) {
        this.master = master;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = App.settings;
            return settings.updateFonts(master);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
