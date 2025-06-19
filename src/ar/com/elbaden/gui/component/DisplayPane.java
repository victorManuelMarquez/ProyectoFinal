package ar.com.elbaden.gui.component;

import javax.swing.*;
import java.awt.*;

public class DisplayPane extends JTextPane {

    public DisplayPane(int rows, int cols) {
        recalculatePreferred(rows, cols);
        setEditable(false);
        setFocusable(false);
        setCaretColor(new Color(0, 0, 0, 0));
        getCaret().setBlinkRate(0);
    }

    public void recalculatePreferred(int rows, int cols) {
        Dimension preferredSize = getPreferredSize();
        FontMetrics metrics = getFontMetrics(getFont());
        Insets insets = getInsets();
        preferredSize.width += metrics.charWidth('m') * cols;
        preferredSize.height += metrics.getHeight() * rows;
        if (insets != null) {
            preferredSize.width += insets.left + insets.right;
            preferredSize.height += insets.top + insets.bottom;
        }
        setPreferredSize(preferredSize);
    }

}
