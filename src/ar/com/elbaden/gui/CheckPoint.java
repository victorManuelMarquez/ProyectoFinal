package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public abstract class CheckPoint implements Callable<String> {

    private final ResourceBundle bundle;

    public CheckPoint() {
        bundle = App.messages;
    }

    protected String buildMessage(String pattern, Object... parameters) {
        if (bundle.containsKey(pattern)) {
            String message = bundle.getString(pattern);
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
