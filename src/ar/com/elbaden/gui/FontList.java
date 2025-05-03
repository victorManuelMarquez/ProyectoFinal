package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class FontList extends JList<Font> {

    private boolean renderWithPreview;

    public FontList(boolean renderWithPreview) {
        this.renderWithPreview = renderWithPreview;
    }

    public FontList() {
        this(false);
        setCellRenderer(new FontCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public boolean isRenderWithPreview() {
        return renderWithPreview;
    }

    public void setRenderWithPreview(boolean renderWithPreview) {
        this.renderWithPreview = renderWithPreview;
        repaint();
        revalidate();
    }

}
