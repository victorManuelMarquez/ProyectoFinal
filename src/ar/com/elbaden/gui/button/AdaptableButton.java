package ar.com.elbaden.gui.button;

import javax.swing.*;
import java.awt.*;

public final class AdaptableButton extends JButton {

    public AdaptableButton(String... values) {
        fitToContent(values);
        if (values.length > 0) {
            setText(values[0]);
        }
    }

    public void fitToContent(String... values) {
        if (values == null) throw new IllegalArgumentException("textValues... == null");
        FontMetrics metrics = getFontMetrics(getFont());
        int width = 0, height = metrics.getHeight();
        Insets defaultInsets = getInsets();
        for (String value : values) {
            width = Math.max(width, metrics.stringWidth(value));
        }
        width += defaultInsets.left + defaultInsets.right;
        height += defaultInsets.top + defaultInsets.bottom;
        setPreferredSize(new Dimension(width, height));
    }

}
