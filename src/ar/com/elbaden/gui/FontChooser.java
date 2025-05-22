package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.Timer;
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
import java.util.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        selectedFont = previewArea.getFont();
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
            searchFonts(searchValue, previewValue);
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

    private JDialog createLoadingDialog(Window owner, SwingWorker<?,?> worker) {
        LoadingDialog loadingDialog = new LoadingDialog(owner, null);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(owner);
        worker.addPropertyChangeListener(loadingDialog);
        if (worker instanceof WindowListener listener) {
            loadingDialog.addWindowListener(listener);
        }
        return loadingDialog;
    }

    private void searchFonts(String searchedValue, String contentPreview) {
        FontSearch search = new FontSearch(familyList);
        search.setSearchedValue(searchedValue);
        search.setContentPreview(contentPreview);
        search.setActualSelection(selectedFont);
        JDialog dialog = createLoadingDialog(this, search);
        search.execute();
        dialog.setVisible(true);
    }

    private void loadFonts(String contentPreview) {
        FontsLoader loader = new FontsLoader(familyList);
        loader.setContentPreview(contentPreview);
        loader.setCurrentFamily(selectedFont != null ? selectedFont.getFamily() : null);
        JDialog dialog = createLoadingDialog(this, loader);
        loader.execute();
        dialog.setVisible(true);
    }

    public static Font createAndShow(Window owner) {
        FontChooser fontChooserDialog = new FontChooser(owner);
        fontChooserDialog.loadFonts(fontChooserDialog.previewText);
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

        private List<Font> allFonts;
        private final SimpleFontCellRenderer simpleFontCellRenderer;
        private final HighlightFontCellRenderer highlightFontCellRenderer;

        public FontList(AbstractListModel<Font> model) {
            super(model);
            allFonts = Collections.emptyList();
            simpleFontCellRenderer = new SimpleFontCellRenderer();
            highlightFontCellRenderer = new HighlightFontCellRenderer();
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setCellRenderer(simpleFontCellRenderer);
        }

        public FontList() {
            this(new DefaultListModel<>());
        }

        public List<Font> getAllFonts() {
            return allFonts;
        }

        public void setAllFonts(List<Font> allFonts) {
            this.allFonts = allFonts;
        }

        public SimpleFontCellRenderer getSimpleFontCellRenderer() {
            return simpleFontCellRenderer;
        }

        public HighlightFontCellRenderer getFontCellRenderer() {
            return highlightFontCellRenderer;
        }

    }

    static class FontFamilyList extends FontList implements DocumentListener {

        private static final String lastSelectionProperty = "lastSelection";
        private static final String searchingFontProperty = "searchingFont";
        private Font lastSelection;
        private String searchedValue;

        public FontFamilyList() {
            addPropertyChangeListener(lastSelectionProperty, getSimpleFontCellRenderer());
            addPropertyChangeListener(lastSelectionProperty, getFontCellRenderer());
            addPropertyChangeListener(searchingFontProperty, getFontCellRenderer());
        }

        public void installFontRenderer() {
            setCellRenderer(getSimpleFontCellRenderer());
        }

        public void installHighlightRenderer() {
            setCellRenderer(getFontCellRenderer());
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

    static class SimpleFontCellRenderer extends DefaultListCellRenderer implements PropertyChangeListener {

        private final Border focusBorder;
        private Border noFocusBorder;
        private Color selectedBgColor;
        private Color selectedFgColor;
        private String lastSelection;

        public SimpleFontCellRenderer() {
            focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
            noFocusBorder = UIManager.getBorder("List.noFocusBorder");
            selectedBgColor = UIManager.getColor("List.selectionBackground");
            selectedFgColor = UIManager.getColor("List.selectionForeground");
            if (UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                noFocusBorder = UIManager.getBorder("List.cellNoFocusBorder");
                selectedBgColor = new Color(57, 105, 138);
                selectedFgColor = Color.WHITE;
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(FontFamilyList.lastSelectionProperty)) {
                setLastSelection(evt.getNewValue() instanceof String value ? value : null);
            }
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
        ) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Font font && component instanceof JLabel label) {
                label.setText(font.getFamily());
                label.setBackground(isSelected ? selectedBgColor : list.getBackground());
                label.setForeground(isSelected ? selectedFgColor : list.getForeground());
                if (getText().equals(getLastSelection())) {
                    setBorder(focusBorder);
                } else {
                    setBorder(cellHasFocus ? focusBorder : noFocusBorder);
                }
            }
            return component;
        }

        public String getLastSelection() {
            return lastSelection;
        }

        public void setLastSelection(String lastSelection) {
            this.lastSelection = lastSelection;
        }

    }

    static class HighlightFontCellRenderer extends JTextField
            implements ListCellRenderer<Font>, PropertyChangeListener {

        private final Border focusBorder;
        private Highlighter.HighlightPainter highlightPainter;
        private Highlighter.HighlightPainter selectionHighlightPainter;
        private Border noFocusBorder;
        private Color selectionBgColor;
        private Color selectionFgColor;
        private String lastFamilyName;
        private String searchedValue;

        public HighlightFontCellRenderer() {
            focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
            noFocusBorder = UIManager.getBorder("List.noFocusBorder");
            selectionBgColor = UIManager.getColor("List.selectionBackground");
            selectionFgColor = UIManager.getColor("List.selectionForeground");
            highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
            if (UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                noFocusBorder = UIManager.getBorder("List.cellNoFocusBorder");
                selectionBgColor = UIManager.getColor("Table[Enabled+Selected].textBackground");
                selectionFgColor = UIManager.getColor("Table[Enabled+Selected].textForeground");
                Color nimbusYellow = new Color(255, 220, 35);
                Color invertedNimbusSelection = new Color(198, 150, 117);
                highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(nimbusYellow);
                selectionHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(invertedNimbusSelection);
            } else if (UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
                Color motifColor = new Color(178, 77, 122);
                selectionHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(motifColor);
            }
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
            setBackground(isSelected ? selectionBgColor : list.getBackground());
            setForeground(isSelected ? selectionFgColor : list.getForeground());
            if (getText().equals(getLastFamilyName())) {
                setBorder(focusBorder);
            } else {
                setBorder(cellHasFocus ? focusBorder : noFocusBorder);
            }
            if (getSearchedValue() != null && !getSearchedValue().equals(getText()) && !getSearchedValue().isBlank()) {
                highlightMatches(isSelected);
            }
            return this;
        }

        private void highlightMatches(boolean isSelected) {
            Highlighter highlighter = getHighlighter();
            highlighter.removeAllHighlights();
            Matcher matcher = Pattern.compile(getSearchedValue(), Pattern.CASE_INSENSITIVE).matcher(getText());
            int pos = 0;
            while (matcher.find(pos) && !matcher.group().isEmpty()) {
                int start = matcher.start();
                int end = matcher.end();
                try {
                    if (!UIManager.getLookAndFeel().getName().equals("Metal")) {
                        highlighter.addHighlight(start, end, isSelected ? selectionHighlightPainter : highlightPainter);
                    } else {
                        highlighter.addHighlight(start, end, highlightPainter);
                    }
                } catch (BadLocationException e) {
                    e.printStackTrace(System.err);
                }
                pos = end;
            }
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
        private final List<String> columnNames;
        private final List<Font> dataList;
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
        private String currentFamily;
        private Font actualFont;

        public FontsLoader(FontFamilyList fontFamilyList) {
            this.fontFamilyList = fontFamilyList;
            boldMatcher = Pattern.compile("(?i)(Black|Bolder|Bold)").matcher("");
            italicMatcher = Pattern.compile("(?i)(Italic|Cursive)").matcher("");
        }

        @Override
        protected DefaultListModel<Font> doInBackground() throws Exception {
            firePropertyChange("indeterminate", false, true);
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] names = environment.getAvailableFontFamilyNames(Locale.getDefault());
            firePropertyChange("indeterminate", true, false);
            DefaultListModel<Font> listModel = new DefaultListModel<>();
            int value = 0;
            List<Font> fonts = new ArrayList<>();
            for (String name : names) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                value++;
                Font font = createFont(name);
                fonts.add(font);
                if (font.getFamily().equals(getCurrentFamily())) {
                    actualFont = font;
                }
                if (canDisplayContent(font, getContentPreview())) {
                    listModel.addElement(font);
                }
                setProgress(value * 100 / names.length);
            }
            getFontFamilyList().setAllFonts(Collections.unmodifiableList(fonts));
            if (actualFont == null && !fonts.isEmpty()) {
                actualFont = fonts.getFirst();
            }
            System.gc(); // despejo la memoria
            return listModel;
        }

        @Override
        protected void done() {
            try {
                fontFamilyList.setModel(get());
                fontFamilyList.setSelectedValue(actualFont, true);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        protected boolean canDisplayContent(Font font, String value) {
            if (value == null || value.isBlank()) {
                return true;
            }
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

        protected FontFamilyList getFontFamilyList() {
            return fontFamilyList;
        }

        public String getContentPreview() {
            return contentPreview;
        }

        public void setContentPreview(String contentPreview) {
            this.contentPreview = contentPreview;
        }

        public String getCurrentFamily() {
            return currentFamily;
        }

        public void setCurrentFamily(String currentFamily) {
            this.currentFamily = currentFamily;
        }

    }

    static class FontSearch extends SwingWorker<DefaultListModel<Font>, Font> implements WindowListener {

        private final FontFamilyList fontFamilyList;
        private String contentPreview;
        private String searchedValue;
        private Font actualSelection;

        public FontSearch(FontFamilyList fontFamilyList) {
            this.fontFamilyList = fontFamilyList;
        }

        @Override
        protected DefaultListModel<Font> doInBackground() throws Exception {
            DefaultListModel<Font> listModel = new DefaultListModel<>();
            List<Font> allFonts = getFontFamilyList().getAllFonts();
            List<Font> sensitiveMatches = new ArrayList<>();
            List<Font> insensitiveMatches = new ArrayList<>();
            int item = 0;
            int total = allFonts.size();
            for (Font font : allFonts) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                item++;
                int actualProgress = item * 100 / total;
                if (canDisplayContent(font, getContentPreview())) {
                    if (getSearchedValue() == null || getSearchedValue().isBlank()) {
                        listModel.addElement(font);
                    } else {
                        String fontFamily = font.getFamily();
                        String regex = Pattern.quote(getSearchedValue());
                        Pattern caseSensitive = Pattern.compile(regex);
                        Pattern caseInsensitive = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                        if (caseSensitive.matcher(fontFamily).find()) {
                            sensitiveMatches.add(font);
                        } else if (caseInsensitive.matcher(fontFamily).find()) {
                            insensitiveMatches.add(font);
                        }
                    }
                }
                setProgress(actualProgress);
            }
            sensitiveMatches.forEach(listModel::addElement);
            insensitiveMatches.forEach(listModel::addElement);
            return listModel;
        }

        @Override
        protected void done() {
            try {
                getFontFamilyList().setModel(get());
                getFontFamilyList().setSelectedValue(getActualSelection(), true);
                if (getSearchedValue() == null || getSearchedValue().isBlank()) {
                    getFontFamilyList().installFontRenderer();
                } else {
                    getFontFamilyList().installHighlightRenderer();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
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

        private boolean canDisplayContent(Font font, String text) {
            if (text == null || text.isBlank()) {
                return true;
            }
            return font.canDisplayUpTo(text) == -1;
        }

        public FontFamilyList getFontFamilyList() {
            return fontFamilyList;
        }

        public String getContentPreview() {
            return contentPreview;
        }

        public void setContentPreview(String contentPreview) {
            this.contentPreview = contentPreview;
        }

        public String getSearchedValue() {
            return searchedValue;
        }

        public void setSearchedValue(String searchedValue) {
            this.searchedValue = searchedValue;
        }

        public Font getActualSelection() {
            return actualSelection;
        }

        public void setActualSelection(Font actualSelection) {
            this.actualSelection = actualSelection;
        }

    }

}
