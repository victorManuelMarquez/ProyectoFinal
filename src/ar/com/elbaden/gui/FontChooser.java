package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FontChooser extends JDialog {

    private static final int DEFAULT_FONT_SIZE = 12;

    private final FontFamilyList familyList;
    private final String previewText;

    private Font selectedFont;
    private int fontSize = DEFAULT_FONT_SIZE;

    private FontChooser(Window owner) {
        super(owner);


        String sizeText = "Tamaño del texto";
        previewText = "El veloz murciélago hindú comía feliz cardillo y kiwi.";

        // componentes
        familyList = new FontFamilyList();
        JScrollPane fontFamilyScrollPane = new JScrollPane();
        JLabel fontSizeLabel = new JLabel(sizeText);
        JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(fontSize, 8, 36, 2));
        JTextArea previewArea = new JTextArea(previewText);
        previewArea.append(String.valueOf(System.lineSeparator()).repeat(5));
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        JScrollPane previewScrollPane = new JScrollPane();
        JButton resetText = new JButton("Restablecer texto");

        // ajustes
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // instalando componentes
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        gbc.insets = new Insets(5, 5, 4, 4);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        fontFamilyScrollPane.getViewport().setView(familyList);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        getContentPane().add(fontFamilyScrollPane, gbc);
        row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridy = row;
        gbc.weighty = .0;
        getContentPane().add(fontSizeLabel, gbc);
        getContentPane().add(fontSizeSpinner, gbc);
        row++;
        previewScrollPane.getViewport().setView(previewArea);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = row;
        gbc.weighty = 1.0;
        getContentPane().add(previewScrollPane, gbc);
        row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.gridy = row;
        gbc.weighty = .0;
        getContentPane().add(resetText, gbc);

        // eventos
        familyList.addListSelectionListener(_ -> {
            if (familyList.getSelectedValue() instanceof Font font) {
                selectedFont = font.deriveFont((float) fontSize);
                previewArea.setFont(selectedFont);
            }
        });

        fontSizeSpinner.addChangeListener(_ -> {
            if (fontSizeSpinner.getValue() instanceof Integer value) {
                fontSize = value;
                Font oldFont = previewArea.getFont();
                selectedFont = oldFont.deriveFont((float) fontSize);
                previewArea.setFont(selectedFont);
            }
        });

        Updater updater = new Updater(_ -> loadFonts(previewArea.getText()));
        previewArea.getDocument().addDocumentListener(updater);

        resetText.addActionListener(_ -> previewArea.setText(previewText));
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

    static class FontFamilyList extends JList<Font> {

        private String[] names;

        public FontFamilyList() {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setCellRenderer(new FontCellRenderer());
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

    }

    static class FontCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
        ) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Font font && renderer instanceof JLabel label) {
                label.setText(font.getFamily());
            }
            return renderer;
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
