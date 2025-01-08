package ar.com.elbaden.utils;

import javax.swing.*;
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

}
