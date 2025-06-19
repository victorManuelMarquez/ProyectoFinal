package ar.com.elbaden.gui;

import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class CheckPoint implements Callable<String> {

    private final ResourceBundle messages;
    private final Object[] values;
    private final LogRecord record;
    private final SimpleAttributeSet attributeSet;
    private Color foregroundColor;

    public CheckPoint(ResourceBundle messages, Object... values) {
        this.messages = messages;
        this.values = values;
        this.record = new LogRecord(Level.OFF, getClass().getName());
        record.setResourceBundle(messages);
        attributeSet = new SimpleAttributeSet();
        foregroundColor = Color.RED;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public Object[] getValues() {
        return values;
    }

    public LogRecord getRecord() {
        return record;
    }

    public SimpleAttributeSet getAttributeSet() {
        return attributeSet;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

}
