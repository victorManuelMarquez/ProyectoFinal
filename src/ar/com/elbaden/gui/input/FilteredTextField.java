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

public final class FilteredTextField
    extends JTextField
        implements ActionListener, DocumentListener, UpdateForegroundByFiltering {

    private final int minimumLength;
    private final Color defaultFgColor;

    public FilteredTextField(String regex, int minimumLength, int maximumLength) {
        if (regex == null) throw new IllegalArgumentException("regex == null");
        if (regex.isBlank()) throw new IllegalArgumentException("regex == \"\"");
        if (minimumLength > maximumLength) throw new IllegalArgumentException("min > max");
        if (minimumLength < 0) throw new IllegalArgumentException("min < 0");
        this.minimumLength = minimumLength;
        defaultFgColor = getForeground();
        ((PlainDocument) getDocument()).setDocumentFilter(new DocumentFilterByRegex(regex, maximumLength));
        // eventos
        addActionListener(this); // muestra un mensaje si el campo no es válido
        getDocument().addDocumentListener(this); // pinta de otro color la fuente si es válido el contenido
    }

    @Override
    public void updateForeground() {
        if (getText().length() < minimumLength) {
            setForeground(outOfBoundFgColor);
        } else {
            setForeground(defaultFgColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getText().length() < minimumLength) {
            ResourceBundle messages;
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
            String title = messages.getString("message_dialog.attention");
            String message = messages.getString("message_dialog.info.min");
            message = MessageFormat.format(message, minimumLength);
            Window window = SwingUtilities.windowForComponent(this);
            MessageDialog.createAndShow(window, title, message, JOptionPane.ERROR_MESSAGE);
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
