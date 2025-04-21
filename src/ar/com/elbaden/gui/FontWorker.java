package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

class FontWorker extends SwingWorker<java.util.List<Font>, Font> {

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    private final JList<Font> fontList;
    private final Window windowAncestor;
    private int maxHeight, maxWidth = 0;

    public FontWorker(JList<Font> fontList) {
        this.fontList = fontList;
        this.windowAncestor = SwingUtilities.getWindowAncestor(fontList);
    }

    @Override
    protected java.util.List<Font> doInBackground() {
        java.util.List<Font> filteredFontList = new ArrayList<>();
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
                if (isCancelled()) {
                    break;
                }
                Font newFont = new Font(family, Font.PLAIN, 12);
                if (isRecommended(newFont, defaultMetrics)) {
                    filteredFontList.add(newFont);
                } else {
                    System.out.println("descartada: " + family);
                }
                setProgress(item * 100 / familiesNames.length);
            }
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
        return filteredFontList;
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            return; // evito CancellationException
        }
        try {
            List<Font> fonts = get();
            DefaultListModel<Font> model = new DefaultListModel<>();
            Font actualFont = getFontList().getFont();
            int index = 0;
            for (Font font : fonts) {
                model.addElement(font);
                if (font.getFamily().equals(actualFont.getFamily())) {
                    index = model.indexOf(font);
                }
            }
            fonts.clear();
            getFontList().setFixedCellWidth(maxWidth + 17); // añado el margen del scroll
            getFontList().setFixedCellHeight(maxHeight + 4); // añado un padding
            getFontList().setModel(model);
            Font selection = model.getElementAt(index); // debe ser un valor contenido en el modelo
            getFontList().setSelectedValue(selection, true);
            SwingUtilities.invokeLater(() -> {
                windowAncestor.pack();
                windowAncestor.setLocationRelativeTo(windowAncestor.getOwner());
            });
        } catch (Exception e) {
            e.printStackTrace(System.err);
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
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
        Rectangle2D newBounds = newFont.getStringBounds(defaultFontFamily, newContext);
        double newFontWidth = newBounds.getWidth();
        double newFontHeight = newBounds.getHeight();

        // se omiten fuentes en blanco tales como 'Adobe Blank' por ejemplo
        if (newFontWidth == 0 || newFontHeight == 0) {
            return false;
        }

        // un pequeño margen de tolerancia tanto positivo como negativo
        int maxOffset = 5;
        int minOffset = 2;

        // se omite cualquier fuente que supere la altura y el ancho tolerables
        if (newFontHeight > defaultBounds.getHeight() + maxOffset ||
                newFontWidth > defaultBounds.getWidth() + maxOffset) {
            return false;
        }

        // se omite cualquier fuente más pequeña fuera de los márgenes tolerables
        if (newFontHeight < defaultBounds.getHeight() - minOffset ||
                newFontWidth < defaultBounds.getWidth() - maxOffset) {
            return false;
        }

        // ahora como el cálcula anterior fue para descartar fuentes demasiado anchas, en este caso se
        // va a actualizar el ancho para contener el nombre propio de la fuente y usarlo para ajustar el
        // ancho de la lista.
        newBounds = newFont.getStringBounds(newFont.getFamily(), newContext);
        newFontWidth = newBounds.getWidth();
        newFontHeight = newBounds.getHeight();

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
