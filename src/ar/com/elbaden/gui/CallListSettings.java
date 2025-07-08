package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CallListSettings implements Callable<String> {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            int total = App.settings.count();
            StringBuilder builder = new StringBuilder();
            App.settings.forEach((key, value) -> {
                builder.append(key);
                builder.append("=");
                builder.append(value);
                builder.append(System.lineSeparator());
            });
            String pattern = App.messages.getString("settings.totalLoaded");
            builder.append(MessageFormat.format(pattern, total));
            return builder.toString();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
