package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
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
import static javax.swing.LayoutStyle.ComponentPlacement;

public class FontChooser extends JDialog {

    private static final int DEFAULT_FONT_SIZE = 12;

    private final FontFamilyList familyList;
    private final String previewText;

    private Font selectedFont;
    private int fontSize = DEFAULT_FONT_SIZE;

    private FontChooser(Window owner) {
        super(owner);

        // variables
        final Integer[] sizes = new Integer[] {
                8, 10, 11, fontSize, 14, 16, 18, 20, 21, 22, 24, 26, 28, 32, 34, 36, 40
        };
        final int minCache = 10;
        String sizeText = "Tamaño del texto";
        previewText = "El veloz murciélago hindú comía feliz cardillo y kiwi.";

        // componentes
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel fontsTab = new JPanel();
        JPanel historyTab = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Buscar");
        JTextField searchField = new JTextField();
        JButton clearSearchBtn = new JButton("Limpiar");
        familyList = new FontFamilyList();
        JScrollPane fontFamilyScrollPane = new JScrollPane();
        FontTable historyTable = new FontTable();
        historyTable.setLimitSize(minCache);
        JScrollPane historyScrollPane = new JScrollPane();
        JLabel fontSizeLabel = new JLabel(sizeText);
        JComboBox<Integer> fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(fontSize);
        fontSizeLabel.setLabelFor(fontSizeCombo);
        JLabel historyCacheLabel = new JLabel("Tamaño de la caché");
        SpinnerNumberModel cacheModel = new SpinnerNumberModel(minCache, minCache, minCache * 4, minCache);
        JSpinner historyCacheSpinner = new JSpinner(cacheModel);
        JButton clearHistoryBtn = new JButton("Limpiar historial");
        JTextArea previewArea = new JTextArea(previewText);
        previewArea.append(String.valueOf(System.lineSeparator()).repeat(5));
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        JScrollPane previewScrollPane = new JScrollPane();
        TitledBorder previewTitledBorder = BorderFactory.createTitledBorder("Vista previa");
        Border previewBorder = previewScrollPane.getBorder();
        previewScrollPane.setBorder(BorderFactory.createCompoundBorder(previewTitledBorder, previewBorder));
        JButton okButton = new JButton("Aceptar");
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
                .addGroup(fontsTabLayout.createSequentialGroup()
                        .addComponent(searchLabel)
                        .addComponent(searchField)
                        .addComponent(clearSearchBtn))
                .addComponent(fontFamilyScrollPane)
                .addGroup(fontsTabLayout.createSequentialGroup()
                        .addComponent(fontSizeLabel)
                        .addComponent(fontSizeCombo, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
        );
        fontsTabLayout.setVerticalGroup(fontsTabLayout.createSequentialGroup()
                .addGroup(fontsTabLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(searchLabel)
                        .addComponent(searchField)
                        .addComponent(clearSearchBtn))
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
                .addGroup(historyTabLayout.createSequentialGroup()
                        .addComponent(historyCacheLabel)
                        .addComponent(historyCacheSpinner, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED, FontTable.DEFAULT_COLUMN_SIZE, Short.MAX_VALUE)
                        .addComponent(clearHistoryBtn))
        );
        historyTabLayout.setVerticalGroup(historyTabLayout.createSequentialGroup()
                .addComponent(historyScrollPane)
                .addGroup(historyTabLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(historyCacheLabel)
                        .addComponent(historyCacheSpinner)
                        .addComponent(clearHistoryBtn))
        );
        tabbedPane.addTab("Historial", historyTab);

        previewScrollPane.getViewport().setView(previewArea);

        mainLayout.setHorizontalGroup(mainLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane)
                .addComponent(previewScrollPane)
                .addGroup(mainLayout.createSequentialGroup()
                        .addPreferredGap(ComponentPlacement.RELATED, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addComponent(cancelBtn))
        );

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
                .addComponent(tabbedPane)
                .addComponent(previewScrollPane)
                .addGroup(mainLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(okButton)
                        .addComponent(cancelBtn))
        );

        // eventos
        Updater listUpdater = new Updater(_ -> {
            String searchValue = searchField.getText();
            String previewValue = previewArea.getText();
            loadFonts(searchValue, previewValue);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Dimension previewScrollableSize = previewScrollPane.getSize();
                previewScrollPane.setPreferredSize(previewScrollableSize);
                previewArea.getDocument().removeDocumentListener(listUpdater);
                previewArea.setText(previewText);
                previewArea.getDocument().addDocumentListener(listUpdater);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                selectedFont = null;
            }
        });

        searchField.getDocument().addDocumentListener(listUpdater);
        searchField.getDocument().addDocumentListener(familyList);

        clearSearchBtn.addActionListener(_ -> searchField.setText(""));

        familyList.addListSelectionListener(_ -> {
            if (familyList.getSelectedValue() instanceof Font font) {
                familyList.setLastSelection(null); // evito marcar otra fuente que no sea la actual
                familyList.repaint(); // evito "ghosting" de la celda marcada anterior
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
                familyList.setLastSelection(selectedFont);
                familyList.clearSelection();
                selectedFont = font;
                fontSize = font.getSize();
                previewArea.setFont(font);
                fontSizeCombo.setSelectedItem(fontSize);
                // restauro los eventos
                fontSizeCombo.addItemListener(sizeSelection);
                previewArea.addPropertyChangeListener("font", previewFontChange);
            } else if (historyTable.getValueAt(selectedRow, selectedColumn) instanceof Integer size) {
                // evito que se agregue al historial nuevamente
                previewArea.removePropertyChangeListener("font", previewFontChange);
                // asigno el tamaño elegido
                fontSize = size;
                fontSizeCombo.setSelectedItem(fontSize);
                // restauro el comportamiento
                previewArea.addPropertyChangeListener("font", previewFontChange);
            }
        });

        historyCacheSpinner.addChangeListener(_ -> {
            if (historyCacheSpinner.getValue() instanceof Integer cacheSize) {
                historyTable.setLimitSize(cacheSize);
            }
        });

        clearHistoryBtn.addActionListener(_ -> historyTable.clear());

        previewArea.getDocument().addDocumentListener(listUpdater);
        previewArea.addPropertyChangeListener("font", previewFontChange);

        okButton.addActionListener(_ -> dispose());

        cancelBtn.addActionListener(_ -> {
            selectedFont = null;
            dispose();
        });
    }

    private JDialog createLoadingDialog(Window owner, FontsLoader fontsLoader) {
        LoadingDialog loadingDialog = new LoadingDialog(owner, null);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(owner);
        fontsLoader.addPropertyChangeListener(loadingDialog);
        loadingDialog.addWindowListener(fontsLoader);
        return loadingDialog;
    }

    private void loadFonts(String searchValue, String previewValue) {
        FontsLoader loader = new FontsLoader(familyList);
        loader.setContentPreview(previewValue);
        loader.setSearchedValue(searchValue);
        loader.setSelectionFont(selectedFont);
        JDialog dialog = createLoadingDialog(this, loader);
        loader.execute();
        dialog.setVisible(true);
    }

    public static Font createAndShow(Window owner) {
        FontChooser fontChooserDialog = new FontChooser(owner);
        fontChooserDialog.loadFonts(null, fontChooserDialog.previewText);
        if (fontChooserDialog.familyList.getModel().getSize() == 0) {
            return null;
        }
        fontChooserDialog.pack();
        fontChooserDialog.setLocationRelativeTo(owner);
        fontChooserDialog.setVisible(true);
        return fontChooserDialog.selectedFont;
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

        private final FontCellRenderer fontCellRenderer;

        public FontList(AbstractListModel<Font> model) {
            super(model);
            fontCellRenderer = new FontCellRenderer();
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setCellRenderer(fontCellRenderer);
        }

        public FontList() {
            this(new DefaultListModel<>());
        }

        public FontCellRenderer getFontCellRenderer() {
            return fontCellRenderer;
        }

    }

    static class FontFamilyList extends FontList implements DocumentListener {

        private static final String lastSelectionProperty = "lastSelection";
        private static final String searchingFontProperty = "searchingFont";
        private String[] names;
        private Font lastSelection;
        private String searchedValue;

        public FontFamilyList() {
            addPropertyChangeListener(lastSelectionProperty, getFontCellRenderer());
            addPropertyChangeListener(searchingFontProperty, getFontCellRenderer());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            paintMatches(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            paintMatches(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}

        public void paintMatches(DocumentEvent event) {
            Document document = event.getDocument();
            try {
                String oldValue = searchedValue;
                searchedValue = document.getText(0, document.getLength());
                firePropertyChange(searchingFontProperty, oldValue, searchedValue);
            } catch (BadLocationException e) {
                e.printStackTrace(System.err);
            }
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

        public Font getLastSelection() {
            return lastSelection;
        }

        public void setLastSelection(Font lastSelection) {
            String oldFamily = getLastSelection() != null ? getLastSelection().getFamily() : null;
            this.lastSelection = lastSelection;
            String newFamily = lastSelection != null ? lastSelection.getFamily() : null;
            firePropertyChange(lastSelectionProperty, oldFamily, newFamily);
        }

    }

    static class FontCellRenderer extends JTextField implements ListCellRenderer<Font>, PropertyChangeListener {

        private final Border focusBorder;
        private final Border noFocusBorder;
        private final Color backgroundColor;
        private final Color foregroundColor;
        private final Color selectionBgColor;
        private final Color selectionFgColor;
        private final Highlighter.HighlightPainter yellowPainter;
        private String lastFamilyName;
        private String searchedValue;

        public FontCellRenderer() {
            focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
            noFocusBorder = UIManager.getBorder("List.noFocusBorder");
            backgroundColor = UIManager.getColor("List.background");
            foregroundColor = UIManager.getColor("List.foreground");
            selectionBgColor = UIManager.getColor("List.selectionBackground");
            selectionFgColor = UIManager.getColor("List.selectionForeground");
            yellowPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
            setBorder(noFocusBorder);
            setEditable(false);
            setFocusable(false);
            setOpaque(true);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(FontFamilyList.lastSelectionProperty)) {
                setLastFamilyName(evt.getNewValue() instanceof String value ? value : null);
            } else if (evt.getPropertyName().equals(FontFamilyList.searchingFontProperty)) {
                setSearchedValue(evt.getNewValue() instanceof String value ? value : null);
            }
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Font> list, Font value, int index, boolean isSelected, boolean cellHasFocus
        ) {
            setText(value.getFamily());
            setBackground(isSelected ? selectionBgColor : backgroundColor);
            setForeground(isSelected ? selectionFgColor : foregroundColor);
            if (getText().equals(getLastFamilyName())) {
                setBorder(focusBorder);
            } else {
                setBorder(cellHasFocus ? focusBorder : noFocusBorder);
            }
            if (getSearchedValue() != null && !getSearchedValue().equals(getText())) {
                int start = getText().indexOf(getSearchedValue());
                if (start >= 0) {
                    int end = getSearchedValue().length();
                    Highlighter highlighter = getHighlighter();
                    highlighter.removeAllHighlights();
                    try {
                        highlighter.addHighlight(start, end, yellowPainter);
                    } catch (BadLocationException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            return this;
        }

        public String getLastFamilyName() {
            return lastFamilyName;
        }

        public void setLastFamilyName(String lastFamilyName) {
            this.lastFamilyName = lastFamilyName;
        }

        public String getSearchedValue() {
            return searchedValue;
        }

        public void setSearchedValue(String searchedValue) {
            this.searchedValue = searchedValue;
        }

    }

    static class FontTable extends JTable {

        public static final int DEFAULT_COLUMN_SIZE = 75;

        private final FontTableModel tableModel;

        public FontTable() {
            tableModel = new FontTableModel();
            setModel(tableModel);
            setFillsViewportHeight(true);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            calculateColumnHeaderWidth();
            setPreferredScrollableViewportSize(new Dimension(DEFAULT_COLUMN_SIZE, DEFAULT_COLUMN_SIZE));
            getColumnModel().getColumn(0).setCellRenderer(new FontTableCellRenderer());
            DefaultTableCellRenderer centeredCellRender = new DefaultTableCellRenderer();
            centeredCellRender.setHorizontalAlignment(SwingConstants.CENTER);
            getColumnModel().getColumn(1).setCellRenderer(centeredCellRender);
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

        public void setLimitSize(int size) {
            tableModel.setLimitSize(size);
        }

    }

    static class FontTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Font font && table instanceof FontTable fontTable) {
                setText(font.getFamily());
                setFont(font);
                FontRenderContext context = getFontMetrics(font).getFontRenderContext();
                Dimension dimension = fontTable.calculateContentDimensions(font, getText(), context);
                JTableHeader header = fontTable.getTableHeader();
                TableColumnModel columnModel = header.getColumnModel();
                TableColumn fontColumn = columnModel.getColumn(column);
                int idealWidth = Math.max(fontColumn.getPreferredWidth(), dimension.width);
                fontColumn.setPreferredWidth(idealWidth);
                fontTable.setRowHeight(row, Math.max(dimension.height, fontTable.getRowHeight()));
            }
            return component;
        }

    }

    static class FontTableModel extends AbstractTableModel {

        private final Class<?>[] columnClasses;
        private final ArrayList<String> columnNames;
        private final ArrayList<Font> dataList;
        private int limitSize;

        public FontTableModel() {
            columnClasses = new Class[] {
                    Font.class,
                    Integer.class
            };
            columnNames = new ArrayList<>();
            columnNames.add("Fuente");
            columnNames.add("Tamaño");
            dataList = new ArrayList<>();
            limitSize = 0;
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
                if (limitReached()) {
                    do {
                        dataList.removeLast();
                    } while (dataList.size() >= getLimitSize());
                }
                String family = font.getFamily();
                Optional<Font> matchedFont;
                matchedFont = dataList.stream().filter(f -> f.getFamily().equals(family)).findFirst();
                matchedFont.ifPresent(dataList::remove);
                dataList.addFirst(font);
                fireTableDataChanged();
            }
        }

        public void removeAll() {
            dataList.clear();
            fireTableDataChanged();
        }

        protected boolean limitReached() {
            if (getLimitSize() <= 0) {
                return false;
            }
            return dataList.size() >= getLimitSize();
        }

        public int getLimitSize() {
            return limitSize;
        }

        public void setLimitSize(int limitSize) {
            this.limitSize = limitSize;
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
        private String searchedValue;

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
            int progress;
            for (String family : fontFamilyList.getNames()) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                value++;
                progress = value * 100 / fontFamilyList.getNames().length;
                if (getSearchedValue() != null && !getSearchedValue().isBlank()) {
                    if (!family.startsWith(getSearchedValue())) {
                        setProgress(progress);
                        continue;
                    }
                }
                Font font = createFont(family);
                if (isSupported(font, getContentPreview())) {
                    if (listFont == null && family.equals(listFamily)) {
                        listFont = font;
                    }
                    fonts.add(font);
                    listModel.addElement(font);
                }
                setProgress(progress);
            }
            if (fonts.isEmpty()) {
                return listModel;
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

        public String getSearchedValue() {
            return searchedValue;
        }

        public void setSearchedValue(String searchedValue) {
            this.searchedValue = searchedValue;
        }

    }

}
