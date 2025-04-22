package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Logger;

class FontWorker extends SwingWorker<FontWorker.Results, Font> {

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    private final JList<Font> admittedList;
    private final JList<Font> discardedList;
    private final Window windowAncestor;
    private final String testValue;
    private int maxHeight, maxWidth = 0;
    private int indexListOne, indexListTwo = -1;

    public FontWorker(JList<Font> admittedList, JList<Font> discardedList) {
        this.admittedList = admittedList;
        this.discardedList = discardedList;
        this.windowAncestor = SwingUtilities.getWindowAncestor(admittedList);
        ResourceBundle messages = ResourceBundle.getBundle(App.MESSAGES);
        this.testValue = messages.getString("dialog.fontChooser.specialSymbols");
    }

    @Override
    protected Results doInBackground() {
        List<Font> admittedFonts = new ArrayList<>();
        List<Font> discardedFonts = new ArrayList<>();
        Font defaultFont = new Font("Dialog", Font.PLAIN, 12); // fuente de referencia
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] familiesNames = ge.getAvailableFontFamilyNames(Locale.getDefault());
            FontMetrics defaultMetrics = getAdmittedList().getFontMetrics(defaultFont);
            FontRenderContext defaultContext = defaultMetrics.getFontRenderContext();
            Rectangle2D rectangle2D = defaultFont.getStringBounds(defaultFont.getFamily(), defaultContext);
            maxWidth = (int) rectangle2D.getWidth();
            maxHeight = (int) rectangle2D.getHeight();
            int item = 0;
            String actualFamily = getAdmittedList().getFont().getFamily();
            Function<String, Boolean> matchFont = family -> family.equals(actualFamily);
            for (String family : familiesNames) {
                item++;
                if (isCancelled()) {
                    break;
                }
                Font newFont = new Font(family, Font.PLAIN, 12);
                if (isRecommended(newFont, defaultMetrics, testValue)) {
                    admittedFonts.add(newFont);
                    indexListOne = matchFont.apply(newFont.getFamily()) ? admittedFonts.size() - 1 : indexListOne;
                } else {
                    discardedFonts.add(newFont);
                    indexListTwo = matchFont.apply(newFont.getFamily()) ? discardedFonts.size() - 1 : indexListTwo;
                }
                setProgress(item * 100 / familiesNames.length);
            }
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
        return new Results(admittedFonts, discardedFonts);
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            return; // evito CancellationException
        }
        try {
            Results results = get();

            DefaultListModel<Font> admittedFontModel = new DefaultListModel<>();
            results.admittedList().forEach(admittedFontModel::addElement);
            getAdmittedList().setFixedCellWidth(maxWidth);
            getAdmittedList().setFixedCellHeight(maxHeight);
            getAdmittedList().setModel(admittedFontModel);

            DefaultListModel<Font> discardedFontModel = new DefaultListModel<>();
            results.rejectedList().forEach(discardedFontModel::addElement);
            FontMetrics metrics = getDiscardedList().getFontMetrics(getDiscardedList().getFont());
            getDiscardedList().setFixedCellHeight(metrics.getHeight());
            getDiscardedList().setModel(discardedFontModel);

            if (indexListOne != -1) {
                getAdmittedList().setSelectedValue(admittedFontModel.getElementAt(indexListOne), true);
            } else if (indexListTwo != -1) {
                getDiscardedList().setSelectedValue(discardedFontModel.getElementAt(indexListTwo), true);
            }

            results.destroy();

            SwingUtilities.invokeLater(() -> {
                windowAncestor.pack();
                windowAncestor.setLocationRelativeTo(windowAncestor.getOwner());
            });
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
    }

    public boolean isRecommended(Font newFont, FontMetrics defaultMetrics, String displayThis) {
        if (displayThis == null) {
            return false;
        }
        if (newFont.canDisplayUpTo(displayThis) >= 0) {
            return false;
        }
        // calculo las dimensiones de la fuente por defecto
        FontRenderContext defaultContext = defaultMetrics.getFontRenderContext();
        Font defaultFont = defaultMetrics.getFont();
        String defaultFontFamily = defaultFont.getFamily();
        Rectangle2D defaultBounds = defaultFont.getStringBounds(defaultFontFamily, defaultContext);

        // usando la lista para calcular las métricas para la nueva fuente
        FontMetrics newMetrics = getAdmittedList().getFontMetrics(newFont);
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
        int maxOffset = 6;
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

    record Results(List<Font> admittedList, List<Font> rejectedList) {

        public void destroy() {
                admittedList().clear();
                rejectedList().clear();
            }

    }

    public JList<Font> getAdmittedList() {
        return admittedList;
    }

    public JList<Font> getDiscardedList() {
        return discardedList;
    }

}
