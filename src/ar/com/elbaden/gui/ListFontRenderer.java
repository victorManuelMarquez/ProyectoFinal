package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

class ListFontRenderer extends DefaultListCellRenderer {

    private final boolean applyFonts;

    public ListFontRenderer(boolean applyFonts) {
        this.applyFonts = applyFonts;
    }

    public ListFontRenderer() {
        this(false);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component render = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Font font) {
            setText(font.getName());
            if (applyFonts) {
                setFont(font);
            }
            // caso especial
            if (UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                // estilo celdas aplicado a este render
                if (index % 2 == 0) {
                    setBackground(UIManager.getColor("Table.background"));
                } else {
                    setBackground(UIManager.getColor("Table.alternateRowColor"));
                }
                // color especial al seleccionar este render de un ítem
                if (isSelected) {
                    setBackground(UIManager.getColor("Table[Enabled+Selected].textBackground"));
                    setForeground(UIManager.getColor("Table[Enabled+Selected].textForeground"));
                }
            }
        }
        return render;
    }

}
