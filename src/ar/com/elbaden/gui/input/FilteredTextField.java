package ar.com.elbaden.gui.input;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;

public final class FilteredTextField extends JTextField implements DocumentListener, UpdateForegroundByFiltering {

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
