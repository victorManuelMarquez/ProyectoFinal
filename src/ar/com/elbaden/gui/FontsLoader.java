package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class FontsLoader extends SwingWorker<List<Font>, Font> implements WindowListener {

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();
    private final LoadingFontsDialog loadingFontsDialog;

    public FontsLoader(LoadingFontsDialog loadingFontsDialog) {
        this.loadingFontsDialog = loadingFontsDialog;
        addPropertyChangeListener(loadingFontsDialog);
        loadingFontsDialog.addWindowListener(this);
    }

    @Override
    protected List<Font> doInBackground() {
        List<Font> fontList = new ArrayList<>();
        try {
            GraphicsEnvironment graphicsEnvironment;
            graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (graphicsEnvironment == null) {
                throw new RuntimeException(GraphicsEnvironment.class.getSimpleName() + "==null");
            }
            String[] families = graphicsEnvironment.getAvailableFontFamilyNames(Locale.getDefault());
            int item = 0;
            for (String family : families) {
                if (isCancelled()) {
                    break;
                }
                Font newFont = createFont(family);
                fontList.add(newFont);
                item++;
                setProgress(item * 100 / families.length);
            }
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
        return fontList;
    }

    @Override
    protected void done() {
        try {
            List<Font> fontList = get();
            getLoadingDataDialog().setResults(fontList);
        } catch (Exception e) {
            GLOBAL_LOGGER.severe(e.getLocalizedMessage());
        }
    }

    public Font createFont(String fontFamily) {
        int defaultSize = 12;
        if (fontFamily.matches("(?i).*Bold|Black.*")) {
            return new Font(fontFamily, Font.BOLD, defaultSize);
        } else if (fontFamily.matches("(?i).*Italic.*")) {
            return new Font(fontFamily, Font.ITALIC, defaultSize);
        } else if (fontFamily.matches("(?i)(?=.*Bold|Black)(?=.*Italic).*")) {
            return new Font(fontFamily, Font.BOLD | Font.ITALIC, defaultSize);
        }
        return new Font(fontFamily, Font.PLAIN, defaultSize);
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        cancel(true);
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    public LoadingFontsDialog getLoadingDataDialog() {
        return loadingFontsDialog;
    }

}
