package ar.com.elbaden.gui.input;

import ar.com.elbaden.gui.modal.MessageDialog;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class FilteredPasswordField
    extends JPasswordField
        implements ActionListener, CheckMinimumLength, DocumentListener, LaunchMessages, UpdateForegroundByFiltering {

    private final int minLength;
    private final Color defaultFgColor;

    public FilteredPasswordField(String regex, int minLength, int maxLength) {
        if (regex == null) throw new IllegalArgumentException("regex == null");
        if (regex.isBlank()) throw new IllegalArgumentException("regex == \"\"");
        if (minLength > maxLength) throw new IllegalArgumentException("min > max");
        if (minLength < 0) throw new IllegalArgumentException("min < 0");
        this.minLength = minLength;
        defaultFgColor = getForeground();
        ((PlainDocument) getDocument()).setDocumentFilter(new DocumentFilterByRegex(this, regex, maxLength));
        // eventos
        addActionListener(this); // muestra un mensaje si el campo no es válido
        getDocument().addDocumentListener(this); // pinta de otro color la fuente si es válido el contenido
    }

    @Override
    public boolean needRevision() {
        return getPassword().length < minLength;
    }

    @Override
    public void showMaximumReached() {}

    @Override
    public void showMinimumNotMet() {
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        String title = messages.getString("message_dialog.attention");
        if (getName() != null) {
            title = getName();
        }
        String message = messages.getString("message_dialog.info.min");
        message = MessageFormat.format(message, minLength);
        Window window = SwingUtilities.windowForComponent(this);
        MessageDialog.createAndShow(window, title, message, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void updateForeground() {
        if (needRevision()) {
            setForeground(outOfBoundFgColor);
        } else {
            setForeground(defaultFgColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (needRevision()) {
            showMinimumNotMet();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateForeground();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateForeground();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {}

}
