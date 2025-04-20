package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FontChooserDialog extends JDialog implements PropertyChangeListener {

    private final JList<Font> availableFontList;
    private final JProgressBar progressBar;
    private Font selectedFont;

    private FontChooserDialog(Window owner) {
        super(owner);
        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);

        // componentes
        availableFontList = new JList<>(new DefaultListModel<>());
        availableFontList.setCellRenderer(new FontRenderer());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(availableFontList);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(scrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        // eventos
        availableFontList.addListSelectionListener(_ -> selectedFont = availableFontList.getSelectedValue());
        availableFontList.addPropertyChangeListener("model", _ -> {
            pack();
            setLocationRelativeTo(getOwner());
        });

        selectedFont = getFont();
    }

    public static Font createAndShow(Window owner) {
        FontChooserDialog dialog = new FontChooserDialog(owner);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        FontWorker worker = new FontWorker(dialog.getAvailableFontList());
        worker.addPropertyChangeListener(dialog);
        worker.execute();
        dialog.setVisible(true);
        return dialog.selectedFont;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            getProgressBar().setValue((Integer) evt.getNewValue());
        }
    }

    static class FontRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            Component render = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Font font) {
                setText(font.getName());
                setFont(font);
                if (UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                    if (index % 2 == 0) {
                        setBackground(UIManager.getColor("Table.background"));
                    } else {
                        setBackground(UIManager.getColor("Table.alternateRowColor"));
                    }
                    if (isSelected) {
                        setBackground(UIManager.getColor("Table[Enabled+Selected].textBackground"));
                        setForeground(UIManager.getColor("Table[Enabled+Selected].textForeground"));
                    }
                }
            }
            return render;
        }

    }

    static class FontWorker extends SwingWorker<List<Font>, Font> {

        private final Cursor defaultCursor;
        private final JList<Font> fontList;
        private int maxHeight, maxWidth = 0;

        public FontWorker(JList<Font> fontList) {
            this.defaultCursor = fontList.getCursor();
            this.fontList = fontList;
        }

        @Override
        protected List<Font> doInBackground() {
            getFontList().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Font> derivedFonts = new ArrayList<>();
            Font defaultFont = new Font("Dialog", Font.PLAIN, 12); // fuente de referencia
            try {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                String[] familiesNames = ge.getAvailableFontFamilyNames(Locale.getDefault());
                FontMetrics defaultMetrics = getFontList().getFontMetrics(defaultFont);
                FontRenderContext defaultContext = defaultMetrics.getFontRenderContext();
                Rectangle2D rectangle2D = defaultFont.getStringBounds(defaultFont.getFamily(), defaultContext);
                maxWidth = (int) rectangle2D.getWidth();
                maxHeight = (int) rectangle2D.getHeight();
                int item = 0;
                for (String family : familiesNames) {
                    item++;
                    Font newFont = new Font(family, Font.PLAIN, 12);
                    if (isRecommended(newFont, defaultMetrics)) {
                        derivedFonts.add(newFont);
                    } else {
                        System.out.println("descartada: " + family);
                    }
                    setProgress(item * 100 / familiesNames.length);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            return derivedFonts;
        }

        @Override
        protected void done() {
            try {
                List<Font> derivedFonts = get();
                DefaultListModel<Font> model = new DefaultListModel<>();
                for (Font font : derivedFonts) {
                    model.addElement(font);
                }
                getFontList().setFixedCellWidth(maxWidth + 17); // añado el margen del scroll
                getFontList().setFixedCellHeight(maxHeight + 4); // añado un padding
                getFontList().setModel(model);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            getFontList().setCursor(defaultCursor);
        }

        public boolean isRecommended(Font newFont, FontMetrics defaultMetrics) {
            if (newFont.canDisplayUpTo(newFont.getFamily()) >= 0) {
                return false;
            }
            // calculo las dimensiones de la fuente por defecto
            FontRenderContext defaultContext = defaultMetrics.getFontRenderContext();
            Font defaultFont = defaultMetrics.getFont();
            String defaultFontFamily = defaultFont.getFamily();
            Rectangle2D defaultBounds = defaultFont.getStringBounds(defaultFontFamily, defaultContext);

            // usando la lista para calcular las métricas para la nueva fuente
            FontMetrics newMetrics = getFontList().getFontMetrics(newFont);
            FontRenderContext newContext = newMetrics.getFontRenderContext();

            // calculo el ancho en píxeles que ocuparía con una palabra, en este caso el nombre de la fuente original
            double newFontWidth = newFont.getStringBounds(defaultFontFamily, newContext).getWidth();
            double newFontHeight = newFont.getStringBounds(defaultFontFamily, newContext).getHeight();

            // se omiten fuentes en blanco tales como 'Adobe Blank' por ejemplo
            if (newFontWidth == 0 || newFontHeight == 0) {
                return false;
            }

            // un pequeño margen de tolerancia tanto positivo como negativo
            int widthOffset = 5;
            int heightOffset = 2;

            // se omite cualquier fuente que supere la altura y el ancho tolerables
            if (newFontHeight > defaultBounds.getHeight() + widthOffset ||
                    newFontWidth > defaultBounds.getWidth() + widthOffset) {
                return false;
            }

            // se omite cualquier fuente más pequeña fuera de los márgenes tolerables
            if (newFontHeight < defaultBounds.getHeight() - heightOffset ||
                    newFontWidth < defaultBounds.getWidth() - widthOffset) {
                return false;
            }

            // ahora como el cálcula anterior fue para descartar fuentes demasiado anchas, en este caso se
            // va a calcular el ancho para contener el nombre propio de la fuente y usarlo para ajustar el
            // ancho de la lista.
            Rectangle2D newRectangle2D = newFont.getStringBounds(newFont.getFamily(), newContext);
            newFontWidth = newRectangle2D.getWidth();
            newFontHeight = newRectangle2D.getHeight();

            // con los datos actualizados se compara y asigna el que será el ancho y alto predefinido para
            // todos los ítems de la lista
            maxWidth = (int) Math.max(newFontWidth, maxWidth);
            maxHeight = (int) Math.max(newFontHeight, maxHeight);

            return newFontWidth <= maxWidth && newFontHeight <= maxHeight;
        }

        public JList<Font> getFontList() {
            return fontList;
        }

    }

    public JList<Font> getAvailableFontList() {
        return availableFontList;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
