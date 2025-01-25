package ar.com.elbaden.gui.input;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class DocumentFilterByRegex extends DocumentFilter {

    private final String regex;
    private final int maximumLength;

    public DocumentFilterByRegex(String regex, int maximumLength) {
        this.regex = regex;
        this.maximumLength = maximumLength;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        String str = fb.getDocument().getText(0, fb.getDocument().getLength());
        String combo = str + text;
        if (combo.matches(getRegex())) {
            if (combo.length() <= getMaximumLength() || (combo.length() - length) <= getMaximumLength()) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    public String getRegex() {
        return regex;
    }

    public int getMaximumLength() {
        return maximumLength;
    }

}
