package ar.com.elbaden.utils;

import ar.com.elbaden.gui.modal.PublishMessage;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public final class Strings {

    private Strings() {
        // ignore
    }

    public static void fitDynamicContent(JComponent component, String... strings) {
        FontMetrics metrics = component.getFontMetrics(component.getFont());
        int width = 0, height;
        Insets insets = component.getInsets();
        for (String value : strings) {
            width = Math.max(width, metrics.stringWidth(value));
        }
        width += insets.left + insets.right;
        height = metrics.getHeight() + insets.top + insets.bottom;
        component.setPreferredSize(new Dimension(width, height));
    }

    // para eliminar próximamente
    @Deprecated
    public static void installDocumentFilterValidator(JTextField field, String regex, int min, int max) {
        String localTitle = "Este campo dice";
        String localFormattedTitle = "El campo %s ha dicho";
        String localFormattedMsg = "Solo se permite hasta %d caracteres máximo.";
        String localFormattedTipMin = "Debe tener al menos %d caracteres.";
        String localFormattedTipMax = "No debe superar los %d caracteres.";

        String message = String.format(localFormattedMsg, max);
        String title = field.getName() == null || field.getName().isBlank() ?
                localTitle : String.format(localFormattedTitle, field.getName());
        int icon = JOptionPane.INFORMATION_MESSAGE;

        if (field.getToolTipText() == null) {
            String tooltip = "<HTML>" + String.format(localFormattedTipMin, min) + "<br>";
            tooltip += String.format(localFormattedTipMax, max) + "</HTML>";
            field.setToolTipText(tooltip);
        }

        AbstractDocument document = (AbstractDocument) field.getDocument();

        document.setDocumentFilter(new DocumentFilter() {
            boolean overflow = false;
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String str = fb.getDocument().getText(0, fb.getDocument().getLength());
                if ((str + text).matches(regex)) {
                    Caret caret = field.getCaret();
                    if ((str + text).length() <= max) {
                        overflow = false;
                        super.replace(fb, offset, length, text, attrs);
                    } else if (caret.getDot() != caret.getMark()) {
                        int beginSelection = Math.min(caret.getDot(), caret.getMark());
                        int endSelection = Math.max(caret.getDot(), caret.getMark());
                        int selection = str.substring(beginSelection, endSelection).length();
                        if ((str.length() - selection) + text.length() <= max) {
                            overflow = false;
                            super.replace(fb, offset, length, text, attrs);
                        } else {
                            overflow = true;
                        }
                    } else {
                        overflow = true;
                    }
                    if (overflow) {
                        PublishMessage.createAndShow(field, message, title, icon);
                    }
                }
            }
        });
    }

}
