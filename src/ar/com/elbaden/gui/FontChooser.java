package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static javax.swing.GroupLayout.*;

public class FontChooser extends JDialog {

    private static final int DEFAULT_FONT_SIZE = 12;

    private final FontFamilyList familyList;
    private final String previewText;

    private Font selectedFont;
    private int fontSize = DEFAULT_FONT_SIZE;

    private FontChooser(Window owner) {
        super(owner);

        // variables
        final Integer[] sizes = new Integer[] {8, 10, 11, 12, 14, 16, 18, 20, 21, 22, 24, 26, 28, 32, 34, 36, 40};
        String sizeText = "Tamaño del texto";
        previewText = "El veloz murciélago hindú comía feliz cardillo y kiwi.";

        // componentes
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel fontsTab = new JPanel();
        JPanel historyTab = new JPanel(new BorderLayout());
        familyList = new FontFamilyList();
        JScrollPane fontFamilyScrollPane = new JScrollPane();
        FontTable historyTable = new FontTable();
        JScrollPane historyScrollPane = new JScrollPane();
        JLabel fontSizeLabel = new JLabel(sizeText);
        JComboBox<Integer> fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(fontSize);
        fontSizeLabel.setLabelFor(fontSizeCombo);
        JButton clearHistoryBtn = new JButton("Limpiar historial");
        JTextArea previewArea = new JTextArea(previewText);
        previewArea.append(String.valueOf(System.lineSeparator()).repeat(5));
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        JScrollPane previewScrollPane = new JScrollPane();
        JButton resetTextBtn = new JButton("Restaurar texto");
        JButton cancelBtn = new JButton("Cancelar");

        // ajustes
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // instalando componentes
        GroupLayout mainLayout = new GroupLayout(getContentPane());
        setLayout(mainLayout);

        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);

        fontFamilyScrollPane.getViewport().setView(familyList);
        GroupLayout fontsTabLayout = new GroupLayout(fontsTab);
        fontsTab.setLayout(fontsTabLayout);
        fontsTabLayout.setAutoCreateContainerGaps(true);
        fontsTabLayout.setAutoCreateGaps(true);
        fontsTabLayout.setHorizontalGroup(fontsTabLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(fontFamilyScrollPane)
                .addGroup(fontsTabLayout.createSequentialGroup()
                        .addComponent(fontSizeLabel)
                        .addComponent(fontSizeCombo, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
        );
        fontsTabLayout.setVerticalGroup(fontsTabLayout.createSequentialGroup()
                .addComponent(fontFamilyScrollPane)
                .addGroup(fontsTabLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(fontSizeLabel)
                        .addComponent(fontSizeCombo))
        );
        tabbedPane.addTab("Fuentes", fontsTab);

        historyScrollPane.getViewport().setView(historyTable);
        GroupLayout historyTabLayout = new GroupLayout(historyTab);
        historyTab.setLayout(historyTabLayout);
        historyTabLayout.setAutoCreateContainerGaps(true);
        historyTabLayout.setAutoCreateGaps(true);
        historyTabLayout.setHorizontalGroup(historyTabLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(historyScrollPane)
                .addComponent(clearHistoryBtn)
        );
        historyTabLayout.setVerticalGroup(historyTabLayout.createSequentialGroup()
                .addComponent(historyScrollPane)
                .addComponent(clearHistoryBtn)
        );
        tabbedPane.addTab("Historial", historyTab);

        previewScrollPane.getViewport().setView(previewArea);

        mainLayout.setHorizontalGroup(mainLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane)
                .addComponent(previewScrollPane)
                .addGroup(mainLayout.createSequentialGroup()
                        .addComponent(resetTextBtn)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelBtn))
        );

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
                .addComponent(tabbedPane)
                .addComponent(previewScrollPane)
                .addGroup(mainLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(resetTextBtn)
                        .addComponent(cancelBtn))
        );

        // eventos
        Updater fontsUpdater = new Updater(_ -> loadFonts(previewArea.getText()));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Dimension previewScrollableSize = previewScrollPane.getSize();
                previewScrollPane.setPreferredSize(previewScrollableSize);
                previewArea.getDocument().removeDocumentListener(fontsUpdater);
                previewArea.setText(previewText);
                previewArea.getDocument().addDocumentListener(fontsUpdater);
            }
        });

        familyList.addListSelectionListener(_ -> {
            if (familyList.getSelectedValue() instanceof Font font) {
                historyTable.getSelectionModel().clearSelection();
                selectedFont = font.deriveFont((float) fontSize);
                previewArea.setFont(selectedFont);
            }
        });

        ItemListener sizeSelection = selectedItem -> {
            if (selectedItem.getItem() instanceof Integer value) {
                fontSize = value;
                Font oldFont = previewArea.getFont();
                selectedFont = oldFont.deriveFont((float) fontSize);
                previewArea.setFont(selectedFont);
            }
        };
        fontSizeCombo.addItemListener(sizeSelection);

        PropertyChangeListener previewFontChange = fontChangeEvent -> {
            Font previousFont = (Font) fontChangeEvent.getOldValue();
            historyTable.addFont(previousFont);
        };

        historyTable.getSelectionModel().addListSelectionListener(_ -> {
            int selectedRow = historyTable.getSelectedRow();
            int selectedColumn = historyTable.getSelectedColumn();
            if (historyTable.getValueAt(selectedRow, selectedColumn) instanceof Font font) {
                // deshabilito los eventos de cambio de fuente
                previewArea.removePropertyChangeListener("font", previewFontChange);
                fontSizeCombo.removeItemListener(sizeSelection);
                // establezco un nuevo escenario
                familyList.clearSelection();
                selectedFont = font;
                fontSize = font.getSize();
                previewArea.setFont(font);
                fontSizeCombo.setSelectedItem(fontSize);
                // restauro los eventos
                fontSizeCombo.addItemListener(sizeSelection);
                previewArea.addPropertyChangeListener("font", previewFontChange);
            }
        });

        clearHistoryBtn.addActionListener(_ -> historyTable.clear());

        previewArea.getDocument().addDocumentListener(fontsUpdater);
        previewArea.addPropertyChangeListener("font", previewFontChange);

        resetTextBtn.addActionListener(_ -> previewArea.setText(previewText));

        cancelBtn.addActionListener(_ -> dispose());
    }

    private JDialog createLoadingDialog(Window owner, FontsLoader fontsLoader) {
        LoadingDialog loadingDialog = new LoadingDialog(owner, null);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(owner);
        fontsLoader.addPropertyChangeListener(loadingDialog);
        loadingDialog.addWindowListener(fontsLoader);
        return loadingDialog;
    }

    private void loadFonts(String previewValue) {
        FontsLoader loader = new FontsLoader(familyList);
        loader.setContentPreview(previewValue);
        loader.setSelectionFont(selectedFont);
        JDialog dialog = createLoadingDialog(this, loader);
        loader.execute();
        dialog.setVisible(true);
    }

    public static void createAndShow(Window owner) {
        FontChooser fontChooserDialog = new FontChooser(owner);
        fontChooserDialog.loadFonts(fontChooserDialog.previewText);
        if (fontChooserDialog.familyList.getModel().getSize() == 0) {
            return;
        }
        fontChooserDialog.pack();
        fontChooserDialog.setLocationRelativeTo(owner);
        fontChooserDialog.setVisible(true);
    }

    static class Updater extends Timer implements DocumentListener {

        public Updater(ActionListener listener) {
            super(500, listener);
            setRepeats(false);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            reset();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            reset();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}

        private void reset() {
            if (isRunning()) {
                restart();
            } else {
                start();
            }
        }

    }

    static class FontList extends JList<Font> {

        public FontList(AbstractListModel<Font> model) {
            super(model);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setCellRenderer(new FontCellRenderer());
        }

        public FontList() {
            this(new DefaultListModel<>());
        }

    }

    static class FontFamilyList extends FontList {

        private String[] names;

        @Override
        public int getVisibleRowCount() {
            return (int) Math.ceil(super.getVisibleRowCount() * 1.5);
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

    }

    static class FontCellRenderer extends DefaultListCellRenderer {

        private final boolean previewFont;

        public FontCellRenderer(boolean previewFont) {
            this.previewFont = previewFont;
        }

        public FontCellRenderer() {
            this(false);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
        ) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Font font && renderer instanceof JLabel label) {
                label.setText(font.getFamily());
                if (isPreviewFont()) {
                    label.setFont(font);
                }
            }
            return renderer;
        }

        public boolean isPreviewFont() {
            return previewFont;
        }

    }

    static class FontTable extends JTable {

        private final FontTableModel tableModel;

        public FontTable() {
            tableModel = new FontTableModel();
            setModel(tableModel);
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            calculateColumnHeaderWidth();
            setPreferredScrollableViewportSize(new Dimension(75, 75));
            getColumnModel().getColumn(0).setCellRenderer(new FontTableCellRenderer());
        }

        public Dimension calculateContentDimensions(Font font, String content, FontRenderContext renderContext) {
            Rectangle2D bounds = font.getStringBounds(content, renderContext);
            int width = (int) Math.ceil(bounds.getWidth());
            int height = (int) Math.ceil(bounds.getHeight());
            return new Dimension(width, height);
        }

        public void calculateColumnHeaderWidth() {
            Font headerFont = getTableHeader().getFont();
            FontRenderContext context = getTableHeader().getFontMetrics(headerFont).getFontRenderContext();
            TableColumnModel tableColumnModel = getTableHeader().getColumnModel();
            for (int col = 0; col < getColumnCount(); col++) {
                String columnName = getColumnName(col);
                Dimension dimension = calculateContentDimensions(headerFont, columnName, context);
                TableColumn column = tableColumnModel.getColumn(col);
                column.setMinWidth(dimension.width);
            }
        }

        public void addFont(Font value) {
            tableModel.addElement(value);
        }

        public void clear() {
            tableModel.removeAll();
            calculateColumnHeaderWidth();
        }

    }

    static class FontTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel label = (JLabel) component;
            if (value instanceof Font font && table instanceof FontTable fontTable) {
                label.setText(font.getFamily());
                label.setFont(font);
                FontRenderContext context = label.getFontMetrics(font).getFontRenderContext();
                Dimension dimension = fontTable.calculateContentDimensions(font, getText(), context);
                JTableHeader header = fontTable.getTableHeader();
                TableColumnModel columnModel = header.getColumnModel();
                TableColumn fontColumn = columnModel.getColumn(column);
                int idealWidth = Math.max(fontColumn.getPreferredWidth(), dimension.width);
                fontColumn.setPreferredWidth(idealWidth);
                fontTable.setRowHeight(row, Math.max(dimension.height, fontTable.getRowHeight()));
            }
            return label;
        }

    }

    static class FontTableModel extends AbstractTableModel {

        private final Class<?>[] columnClasses;
        private final ArrayList<String> columnNames;
        private final ArrayList<Font> dataList;

        public FontTableModel() {
            columnClasses = new Class[] {
                    Font.class,
                    Integer.class
            };
            columnNames = new ArrayList<>();
            columnNames.add("Fuente");
            columnNames.add("Tamaño");
            dataList = new ArrayList<>();
        }

        @Override
        public String getColumnName(int column) {
            return columnNames.get(column);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClasses[columnIndex];
        }

        @Override
        public int getRowCount() {
            return dataList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (dataList.isEmpty()) {
                return null;
            }
            if (rowIndex < 0 || rowIndex >= dataList.size()) {
                return null;
            }
            if (columnIndex < 0 || columnIndex >= columnNames.size()) {
                return null;
            }
            if (getColumnClass(columnIndex) == Font.class) {
                return dataList.get(rowIndex);
            } else if (getColumnClass(columnIndex) == Integer.class) {
                return dataList.get(rowIndex).getSize();
            } else {
                return null;
            }
        }

        public void addElement(Font font) {
            if (!dataList.contains(font)) {
                String family = font.getFamily();
                Optional<Font> matchedFont;
                matchedFont = dataList.stream().filter(f -> f.getFamily().equals(family)).findFirst();
                matchedFont.ifPresent(dataList::remove);
                dataList.add(font);
                fireTableDataChanged();
            }
        }

        public void removeAll() {
            dataList.clear();
            fireTableDataChanged();
        }

    }

    static class LoadingDialog extends JDialog implements PropertyChangeListener {

        private final Cursor defaultCursor;
        private final JProgressBar progressBar;

        public LoadingDialog(Window owner, String title) {
            super(owner, title);
            // ajustes
            setModal(true);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
                setUndecorated(true);
            }
            BorderLayout borderLayout = (BorderLayout) getLayout();
            borderLayout.setHgap(0); // limpio los márgenes
            borderLayout.setVgap(0); // limpio los márgenes

            // componentes
            defaultCursor = getCursor();
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);

            // instalando componentes
            getContentPane().add(progressBar, BorderLayout.SOUTH);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("state".equals(evt.getPropertyName())) {
                if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                } else if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    setCursor(defaultCursor);
                    setVisible(false);
                    dispose();
                }
            } else if ("progress".equals(evt.getPropertyName())) {
                Integer newValue = (Integer) evt.getNewValue();
                progressBar.setValue(newValue);
            } else if ("indeterminate".equals(evt.getPropertyName())) {
                boolean newValue = (boolean) evt.getNewValue();
                progressBar.setIndeterminate(newValue);
            }
        }

    }

    static class FontsLoader extends SwingWorker<DefaultListModel<Font>, String> implements WindowListener {

        private final FontFamilyList fontFamilyList;
        private final Matcher boldMatcher;
        private final Matcher italicMatcher;
        private String contentPreview;
        private Font selectionFont;

        public FontsLoader(FontFamilyList fontFamilyList) {
            this.fontFamilyList = fontFamilyList;
            boldMatcher = Pattern.compile("(?i)(Black|Bolder|Bold)").matcher("");
            italicMatcher = Pattern.compile("(?i)(Italic|Cursive)").matcher("");
        }

        @Override
        protected DefaultListModel<Font> doInBackground() throws Exception {
            firePropertyChange("indeterminate", false, true);
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontFamilyList.setNames(environment.getAvailableFontFamilyNames(Locale.getDefault()));
            firePropertyChange("indeterminate", true, false);
            DefaultListModel<Font> listModel = new DefaultListModel<>();
            int value = 0;
            ArrayList<Font> fonts = new ArrayList<>();
            String listFamily = fontFamilyList.getFont().getFamily();
            Font listFont = null;
            for (String family : fontFamilyList.getNames()) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                value++;
                Font font = createFont(family);
                if (isSupported(font, getContentPreview())) {
                    if (listFont == null && family.equals(listFamily)) {
                        listFont = font;
                    }
                    fonts.add(font);
                    listModel.addElement(font);
                }
                setProgress(value * 100 / fontFamilyList.getNames().length);
            }
            Stream<Font> stream = fonts.parallelStream();
            Font firstElement = listModel.getElementAt(0);
            // sí se definió una fuente previa
            if (getSelectionFont() != null) {
                String family = getSelectionFont().getFamily();
                // se busca la fuente establecida en la nueva colección
                Optional<Font> result = stream.filter(f -> f.getFamily().equals(family)).findFirst();
                // se establece la misma fuente o el primer elemento si está no existe, pudiendo ser null
                setSelectionFont(result.orElse(firstElement));
            } else if (listFont != null) {
                // sí la fuente previa no está establecida se usará la fuente de la lista
                // verificando de antemano si puede renderizar el texto establecido
                setSelectionFont(listFont);
            } else {
                // sí no fue posible establecer una fuente con los criterios anteriores se establecerá
                // el primer elemento o null si no hay tal
                setSelectionFont(firstElement);
            }
            return listModel;
        }

        @Override
        protected void done() {
            try {
                fontFamilyList.setModel(get());
                fontFamilyList.setSelectedValue(getSelectionFont(), true);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        public boolean isSupported(Font font, String value) {
            return font.canDisplayUpTo(value) == -1;
        }

        private Font createFont(String familyName) {
            if (boldMatcher.reset(familyName).find()) {
                if (italicMatcher.reset(familyName).find()) {
                    return new Font(familyName, Font.BOLD|Font.ITALIC, DEFAULT_FONT_SIZE);
                }
                return new Font(familyName, Font.BOLD, DEFAULT_FONT_SIZE);
            } else if (italicMatcher.reset(familyName).find()) {
                return new Font(familyName, Font.ITALIC, DEFAULT_FONT_SIZE);
            }
            return new Font(familyName, Font.PLAIN, DEFAULT_FONT_SIZE);
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

        public String getContentPreview() {
            return contentPreview;
        }

        public void setContentPreview(String contentPreview) {
            this.contentPreview = contentPreview;
        }

        public Font getSelectionFont() {
            return selectionFont;
        }

        public void setSelectionFont(Font selectionFont) {
            this.selectionFont = selectionFont;
        }

    }

}
