package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class FontCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        Component original = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (original instanceof JLabel label && value instanceof Font font) {
            label.setText(font.getName());
            // Nimbus no renderiza correctamente un render personalizado, incluso si se extiende desde el render
            // por defecto. Esta es la única solución práctica que conozco para esta situación.
            if (UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                if (isSelected) {
                    label.setBackground(UIManager.getColor("Table[Enabled+Selected].textBackground"));
                    label.setForeground(UIManager.getColor("Table[Enabled+Selected].textForeground"));
                } else {
                    label.setBackground(list.getBackground()); // renderiza el color correctamente aquí
                    label.setForeground(list.getForeground()); // renderiza correctamente
                }
            }
        }
        return original;
    }

}
