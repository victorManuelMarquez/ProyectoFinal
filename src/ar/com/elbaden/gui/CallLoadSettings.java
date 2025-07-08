package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CallLoadSettings implements Callable<String> {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Settings settings = App.settings;
            return settings.load();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
