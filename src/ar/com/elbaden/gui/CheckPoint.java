package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

public abstract class CheckPoint implements Callable<String> {

    private Message message;

    public CheckPoint() {
        message = null;
    }

    protected String buildMessage(String pattern, Object... parameters) {
        message = new Message(pattern, parameters);
        return message.toString();
    }

    public static class Message {

        private final String pattern;
        private final Object[] parameters;

        private Message(String pattern, Object... parameters) {
            this.pattern = pattern;
            this.parameters = parameters;
        }

        @Override
        public String toString() {
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

        public String getPattern() {
            return pattern;
        }

        public Object[] getParameters() {
            return parameters;
        }

    }

    public Message getMessage() {
        return message;
    }

}
