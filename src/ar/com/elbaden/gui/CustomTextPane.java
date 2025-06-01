package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class CustomTextPane extends JTextPane {

    public static final int DEFAULT_COLS = 24;
    public static final int DEFAULT_ROWS = 6;
    private int rows, cols;

    public CustomTextPane() {
        this(new DefaultStyledDocument(), DEFAULT_ROWS, DEFAULT_COLS);
    }

    public CustomTextPane(StyledDocument styledDocument, int rows, int cols) {
        super(styledDocument);
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        FontMetrics metrics = getFontMetrics(getFont());
        Insets insets = getInsets();
        int widthMargins = 0, heightMargins = 0;
        if (insets != null) {
            widthMargins = insets.left + insets.right;
            heightMargins = insets.top + insets.bottom;
        }
        size.width = metrics.charWidth('m') * getCols() + widthMargins;
        size.height = metrics.getHeight() * getRows() + heightMargins;
        return size;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        rows = 0;
        cols = 0;
    }

    public int getRows() {
        if (rows == 0) {
            FontMetrics metrics = getFontMetrics(getFont());
            rows = metrics.getHeight();
        }
        return rows;
    }

    public void setRows(int rows) {
        int old = this.rows;
        if (rows < 0) {
            throw new IllegalArgumentException(rows + " < 0");
        }
        if (rows != old) {
            this.rows = rows;
            invalidate();
        }
    }

    public int getCols() {
        if (cols == 0) {
            FontMetrics metrics = getFontMetrics(getFont());
            cols = metrics.charWidth('m');
        }
        return cols;
    }

    public void setCols(int cols) {
        int old = this.cols;
        if (old < 0) {
            throw new IllegalArgumentException(old + " < 0");
        }
        if (cols != old) {
            this.cols = cols;
            invalidate();
        }
    }

}
