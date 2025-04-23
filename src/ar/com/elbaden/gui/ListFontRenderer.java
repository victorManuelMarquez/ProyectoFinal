package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class ListFontRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        Component original = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (original instanceof JLabel label && value instanceof Font font) {
            label.setText(font.getName());
        }
        return original;
    }

}
