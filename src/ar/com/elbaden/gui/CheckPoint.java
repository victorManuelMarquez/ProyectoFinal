package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class CheckPoint implements Callable<String> {

    private final ResourceBundle bundle;
    private final LogRecord logRecord;

    public CheckPoint() {
        bundle = App.messages;
        logRecord = new LogRecord(Level.OFF, null);
    }

    protected String buildMessage(Level level, String pattern, Object... parameters) {
        logRecord.setLevel(level);
        logRecord.setMessage(pattern);
        logRecord.setResourceBundle(bundle);
        logRecord.setParameters(parameters.length > 0 ? parameters : null);
        return buildPublicMessage(pattern, parameters);
    }

    protected String buildPublicMessage(String pattern, Object... parameters) {
        if (bundle.containsKey(pattern)) {
            String message = bundle.getString(pattern);
            if (parameters.length > 0) {
                Object[] objects = new Object[parameters.length];
                int i = 0;
                for (Object parameter : parameters) {
                    if (parameter instanceof File file) {
                        objects[i] = file.getName();
                    } else if (parameter instanceof UIManager.LookAndFeelInfo info) {
                        objects[i] = info.getName();
                    } else {
                        objects[i] = parameter;
                    }
                    i++;
                }
                return MessageFormat.format(message, objects);
            } else {
                return message;
            }
        } else {
            return pattern;
        }
    }

    public LogRecord getLogRecord() {
        return logRecord;
    }

}
