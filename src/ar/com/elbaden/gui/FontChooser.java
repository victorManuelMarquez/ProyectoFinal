package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FontChooser extends JDialog {

    private final FontFamilyList familyList;
    private final Matcher boldMatcher;
    private final Matcher italicMatcher;
    private Font selectedFont;
    private int fontSize = 12;

    private FontChooser(Window owner) {
        super(owner);
        boldMatcher = Pattern.compile("(?i)(Black|Bolder|Bold)").matcher("");
        italicMatcher = Pattern.compile("(?i)(Italic|Cursive)").matcher("");

        String sizeText = "Tamaño del texto";
        String previewText = "El veloz murciélago hindú comía feliz cardillo y kiwi.";
        previewText += String.valueOf(System.lineSeparator()).repeat(5);

        // componentes
        familyList = new FontFamilyList();
        JScrollPane fontFamilyScrollPane = new JScrollPane();
        JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(fontSize, 8, 36, 2));
        JTextArea previewArea = new JTextArea(previewText);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        JScrollPane previewScrollPane = new JScrollPane();

        // ajustes
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // instalando componentes
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        gbc.insets = new Insets(5, 5, 4, 4);
        fontFamilyScrollPane.getViewport().setView(familyList);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        getContentPane().add(fontFamilyScrollPane, gbc);
        row++;
        JLabel fontSizeLabel = new JLabel(sizeText);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridy = row;
        getContentPane().add(fontSizeLabel, gbc);
        getContentPane().add(fontSizeSpinner, gbc);
        row++;
        previewScrollPane.getViewport().setView(previewArea);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = row;
        getContentPane().add(previewScrollPane, gbc);

        // eventos
        familyList.addListSelectionListener(_ -> {
            if (familyList.getSelectedValue() instanceof String family) {
                selectedFont = createFont(family);
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
    }

    private Font createFont(String familyName) {
        if (boldMatcher.reset(familyName).find()) {
            if (italicMatcher.reset(familyName).find()) {
                return new Font(familyName, Font.BOLD|Font.ITALIC, fontSize);
            }
            return new Font(familyName, Font.BOLD, fontSize);
        } else if (italicMatcher.reset(familyName).find()) {
            return new Font(familyName, Font.ITALIC, fontSize);
        }
        return new Font(familyName, Font.PLAIN, fontSize);
    }

    public static void createAndShow(Window owner) {
        FontChooser fontChooserDialog = new FontChooser(owner);
        LoadingDialog loadingDialog = new LoadingDialog(owner, null);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(owner);
        FontsLoader fontsLoader = new FontsLoader(fontChooserDialog.familyList);
        fontsLoader.addPropertyChangeListener(loadingDialog);
        loadingDialog.addWindowListener(fontsLoader);
        fontsLoader.execute();
        loadingDialog.setVisible(true);
        if (fontChooserDialog.familyList.getModel().getSize() == 0) {
            return;
        }
        fontChooserDialog.pack();
        fontChooserDialog.setLocationRelativeTo(owner);
        fontChooserDialog.setVisible(true);
    }

    static class FontFamilyList extends JList<String> {

        private String[] names;

        public FontFamilyList() {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
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

    static class FontsLoader extends SwingWorker<DefaultListModel<String>, String> implements WindowListener {

        private final FontFamilyList fontFamilyList;

        public FontsLoader(FontFamilyList fontFamilyList) {
            this.fontFamilyList = fontFamilyList;
        }

        @Override
        protected DefaultListModel<String> doInBackground() throws Exception {
            firePropertyChange("indeterminate", false, true);
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontFamilyList.setNames(environment.getAvailableFontFamilyNames(Locale.getDefault()));
            firePropertyChange("indeterminate", true, false);
            DefaultListModel<String> listModel = new DefaultListModel<>();
            int value = 0;
            for (String family : fontFamilyList.getNames()) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                value++;
                listModel.addElement(family);
                setProgress(value * 100 / fontFamilyList.getNames().length);
            }
            return listModel;
        }

        @Override
        protected void done() {
            try {
                fontFamilyList.setModel(get());
                String family = fontFamilyList.getFont().getFamily();
                fontFamilyList.setSelectedValue(family, true);
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

    }

}
