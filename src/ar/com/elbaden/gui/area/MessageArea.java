package ar.com.elbaden.gui.area;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public final class MessageArea extends JTextArea implements DocumentListener {

    private int width = 0, height = 0;

    public MessageArea() {
        setBackground(new Color(255, 255, 255, 0));
        setEditable(false);
        setFocusable(false);
        setFont(UIManager.getFont("Label.font"));
        setDoubleBuffered(true);
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!getText().isBlank()) {
            FontMetrics metrics = getFontMetrics(getFont());
            String[] strings = getText().split("(\r\n|\n)");
            width = metrics.stringWidth(getText());
            height = metrics.getHeight() * (strings.length + 1);
            int lines = 1;
            for (String str : strings) {
                if (!str.isBlank()) {
                    width += metrics.stringWidth(str);
                    lines++;
                }
            }
            width = width / lines;
            width += UIManager.getInt("ScrollBar.width");
            width = Math.min(width, 640);
            height = Math.min(height, 480);

            if (strings.length > 1) {
                setPreferredSize(getMinimumSize());
            } else if (width < 640 && height < 480) {
                setWrapStyleWord(false);
                setLineWrap(false);
            }
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {}

    @Override
    public void changedUpdate(DocumentEvent e) {}

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

}
