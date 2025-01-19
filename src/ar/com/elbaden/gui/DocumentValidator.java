package ar.com.elbaden.gui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;

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
