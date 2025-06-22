package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class CheckPoint implements Callable<String> {

    private final LogRecord logRecord;

    public CheckPoint() {
        logRecord = new LogRecord(Level.OFF, "");
        logRecord.setResourceBundle(App.messages);
        logRecord.setResourceBundleName(App.messages.getBaseBundleName());
    }

    protected String buildMessages(Level level, String pattern, Object... parameters) {
        getLogRecord().setLevel(level);
        getLogRecord().setMessage(pattern);
        getLogRecord().setParameters(parameters.length > 0 ? parameters : null);
        if (parameters.length > 0) {
            return buildSimpleMessage(pattern, parameters);
        } else if (getLogRecord().getResourceBundle().containsKey(pattern)) {
            return getLogRecord().getResourceBundle().getString(pattern);
        } else {
            return pattern;
        }
    }

    protected String buildSimpleMessage(String pattern, Object... parameters) {
        if (parameters.length == 0) {
            return pattern;
        }
        int item = 0;
        StringBuilder builder = new StringBuilder();
        for (Object parameter : parameters) {
            item++;
            if (parameter instanceof File file) {
                builder.append(MessageFormat.format(pattern, file.getName()));
            } else {
                builder.append(MessageFormat.format(pattern, parameter));
            }
            if (item != parameters.length) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public LogRecord getLogRecord() {
        return logRecord;
    }

}
