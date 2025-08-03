package ar.com.elbaden.gui.component;

import javax.swing.*;
import java.awt.*;

@Deprecated
public class FontListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel label && value instanceof Font font) {
            label.setText(font.getFamily());
        }
        return c;
    }

}
