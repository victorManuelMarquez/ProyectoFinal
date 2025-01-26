package ar.com.elbaden.gui.input;

import ar.com.elbaden.gui.modal.MessageDialog;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class DocumentFilterByRegex extends DocumentFilter implements LaunchMessages {

    private final JComponent component;
    private final String regex;
    private final int maxLength;

    public DocumentFilterByRegex(JComponent component, String regex, int maxLength) {
        this.component = component;
        this.regex = regex;
        this.maxLength = maxLength;
    }

    @Override
    public void showMaximumReached() {
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        String title = messages.getString("message_dialog.attention");
        String message = messages.getString("message_dialog.info.max");
        message = MessageFormat.format(message, maxLength);
        Window window = SwingUtilities.windowForComponent(component);
        MessageDialog.createAndShow(window, title, message, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMinimumNotMet() {}

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        String str = fb.getDocument().getText(0, fb.getDocument().getLength());
        String combo = str + text;
        if (combo.matches(getRegex())) {
            if (combo.length() <= getMaxLength() || (combo.length() - length) <= getMaxLength()) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                showMaximumReached();
            }
        }
    }

    public String getRegex() {
        return regex;
    }

    public int getMaxLength() {
        return maxLength;
    }

}
