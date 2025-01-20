package ar.com.elbaden.gui;

import ar.com.elbaden.gui.modal.PublishMessage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;

public final class DocumentValidator extends PlainDocument {

    public DocumentValidator(String regex, int max) {
        this(regex, 0, max);
    }

    public DocumentValidator(String regex, int min, int max) {
        if (max <= min)
            throw new IllegalArgumentException("max <= min");
        else if (min < 0)
            throw new IllegalArgumentException("min < 0");
        setDocumentFilter(new FilterByRegex(regex, max));
        getDocumentProperties().put("minLength", min);
        getDocumentProperties().put("maxLength", max);
        getDocumentProperties().put("canBeEmpty", (min == 0));
    }

    public ChangeListener getChangeListener() {
        return (ChangeListener) getDocumentFilter();
    }

    public String getSuggestedTooltip() {
        String localFormattedMax = "Máx. %d caracteres.";
        String htmlFormat = "<HTML>";
        if (!(Boolean) getDocumentProperties().get("canBeEmpty")) {
            String localFormattedMin = "Mín. %d caracteres.";
            htmlFormat += String.format(localFormattedMin, getProperty("minLength")) + "<br>";
        }
        htmlFormat += String.format(localFormattedMax, getProperty("maxLength"));
        return htmlFormat + "</HTML>";
    }

    public static boolean verificationNeeded(JTextField field) {
        if (field == null)
            throw new IllegalArgumentException("field == null");
        String value = field.getText();
        if (field instanceof JPasswordField passwordField)
            value = new String(passwordField.getPassword());
        DocumentValidator validator;
        try {
            validator = (DocumentValidator) field.getDocument();
        } catch (ClassCastException e) {
            throw new RuntimeException(e.getMessage());
        }
        boolean canBeEmpty = (Boolean) validator.getProperty("canBeEmpty");
        if (!canBeEmpty) {
            Component rootComponent = SwingUtilities.getRoot(field);
            String localFormattedTitle = "El campo \"%s\" ha dicho:";
            int min = (Integer) validator.getProperty("minLength");
            int icon = JOptionPane.ERROR_MESSAGE;
            if (value.length() < min) {
                String localFormattedMsg = "Debe tener %d caracteres mínimo.";
                String message = String.format(localFormattedMsg, min);
                String title = String.format(localFormattedTitle, field.getName());
                PublishMessage.createAndShow(rootComponent, message, title, icon);
                field.requestFocusInWindow();
                return true;
            }
        }
        return false;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name == null");
        else if (name.isBlank())
            throw new IllegalArgumentException("name == \"\"");
        getDocumentProperties().put("fieldName", name);
    }

    public void setRoot(Window root) {
        if (root == null) return;
        getDocumentProperties().put("root", root);
    }

    public static class FilterByRegex extends DocumentFilter implements ChangeListener {

        private final String regex;
        private final int max;
        private boolean isSelection = false;
        private int beginSelection = 0, endSelection = 0;

        public FilterByRegex(String regex, int max) {
            this.regex = regex;
            this.max = max;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String str = fb.getDocument().getText(0, fb.getDocument().getLength());
            if (text.matches(getRegex()) && (str + text).matches(getRegex())) {
                boolean isValid;
                if (isSelection) {
                    int selection = str.substring(beginSelection, endSelection).length();
                    isValid = (str.length() - selection) + text.length() <= max;
                } else {
                    isValid = (str + text).length() <= max;
                }
                if (isValid) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    String name = fb.getDocument().getProperty("fieldName").toString();
                    Component component = (Component) fb.getDocument().getProperty("root");
                    String localFormattedTitle = "El campo \"%s\" ha dicho:";
                    String localFormattedMessage = "Solo se permite %d caracteres máximo.";
                    String title = String.format(localFormattedTitle, name);
                    String message = String.format(localFormattedMessage, max);
                    PublishMessage.createAndShow(component, message, title, JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() instanceof Caret caret) {
                isSelection = caret.getDot() != caret.getMark();
                if (isSelection) {
                    beginSelection = Math.min(caret.getDot(), caret.getMark());
                    endSelection = Math.max(caret.getDot(), caret.getMark());
                }
            }
        }

        public String getRegex() {
            return regex;
        }

    }

}
