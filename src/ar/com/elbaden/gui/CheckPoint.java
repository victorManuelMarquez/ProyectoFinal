package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

public abstract class CheckPoint implements Callable<String> {

    protected String buildMessage(String pattern, Object... parameters) {
        if (App.messages.containsKey(pattern)) {
            String message = App.messages.getString(pattern);
            if (parameters.length > 0) {
                return MessageFormat.format(message, parameters);
            } else {
                return message;
            }
        } else {
            return pattern;
        }
    }

}
