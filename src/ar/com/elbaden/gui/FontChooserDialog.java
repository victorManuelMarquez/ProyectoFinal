package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
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
            List<Font> derivedFonts = new ArrayList<>();
            FontRenderContext context = new FontRenderContext(null, true, true);
            try {
                getFontList().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                String[] familiesNames = ge.getAvailableFontFamilyNames(Locale.getDefault());
                int item = 0;
                for (String family : familiesNames) {
                    item++;
                    Font newFont = new Font(family, Font.PLAIN, 12);
                    if (newFont.canDisplayUpTo(family) == -1) {
                        LineMetrics lineMetrics = newFont.getLineMetrics(family, context);
                        maxHeight = (int) Math.max(lineMetrics.getHeight(), maxHeight);
                        maxWidth = (int) Math.max(newFont.getStringBounds(family, context).getWidth(), maxWidth);
                        derivedFonts.add(newFont);
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
                getFontList().setFixedCellHeight(maxHeight);
                getFontList().setModel(model);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            getFontList().setCursor(defaultCursor);
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
