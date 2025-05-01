package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Function;

public class FontChooserDialog extends JDialog {

    private final FontFinder fontFinder;
    private final JList<Font> fontList;
    private Font selectedFont;

    public FontChooserDialog(Window owner) {
        super(owner);
        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Font Chooser");
        setModal(true);
        setLayout(new GridBagLayout());

        // componentes
        JLabel searchLabel = new JLabel("Buscar");
        fontFinder = new FontFinder();
        fontList = new JList<>();
        fontList.setCellRenderer(new FontCellRenderer());
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane fontListScrollPane = new JScrollPane();
        JLabel fontSizeLabel = new JLabel("Tamaño de fuente");
        JSpinner fontSize = new JSpinner(new SpinnerNumberModel(12, 8, 36, 2));
        JTextArea previewTextArea = new JTextArea("El veloz murciélago hindú comía feliz cardillo y kiwi.");
        previewTextArea.setLineWrap(true);
        previewTextArea.setWrapStyleWord(true);
        JScrollPane previewAreaScrollPane = new JScrollPane();

        // amplío el alto de previsualización
        previewTextArea.setText(previewTextArea.getText() + String.valueOf(System.lineSeparator()).repeat(5));

        // instalando componentes
        fontListScrollPane.getViewport().setView(fontList);
        previewAreaScrollPane.getViewport().setView(previewTextArea);

        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        gbc.insets = new Insets(3, 3, 2, 2); // sup + inf | izq + der = 5px
        getContentPane().add(searchLabel, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        getContentPane().add(fontFinder, gbc);
        row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = row;
        gbc.weighty = 1.0;
        getContentPane().add(fontListScrollPane, gbc);
        row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 2;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        getContentPane().add(fontSizeLabel, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        getContentPane().add(fontSize, gbc);
        row++;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        getContentPane().add(previewAreaScrollPane, gbc);

        // eventos
        fontFinder.addActionListener(e -> {
            if ("comboBoxEdited".equals(e.getActionCommand())) {
                Font selected = (Font) fontFinder.getModel().getSelectedItem();
                Font previous = fontList.getSelectedValue();
                if (selected != null && previous != selected) {
                    fontList.setSelectedValue(selected, true);
                }
            }
        });

        Function<Font, Font> deriveFontSize = f -> f.deriveFont(Float.parseFloat(fontSize.getValue().toString()));

        fontList.addListSelectionListener(_ -> {
            Font value = fontList.getSelectedValue();
            if (value != null) {
                value = deriveFontSize.apply(value);
                previewTextArea.setFont(value);
            }
        });

        fontSize.addChangeListener(_ -> {
            Font previewFont = previewTextArea.getFont();
            previewFont = deriveFontSize.apply(previewFont);
            previewTextArea.setFont(previewFont);
        });

        PropertyChangeListener previewListener = _ -> selectedFont = previewTextArea.getFont();
        previewTextArea.addPropertyChangeListener("font", previewListener);

        // valor de retorno
        selectedFont = getFont(); // puede retornar null CUIDADO!
    }

    public static Font createAndShow(Window owner) {
        FontChooserDialog dialog = new FontChooserDialog(owner);
        // recolecto los resultados obtenidos y muestro el diálogo principal
        List<Font> results = createLoadingDialog(dialog);
        dialog.loadFonts(results); // necesito los componentes cargados para mostrarlos correctamente en pantalla
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        return dialog.selectedFont;
    }

    private static List<Font> createLoadingDialog(FontChooserDialog owner) {
        LoadingFontsDialog dialog = new LoadingFontsDialog(owner, "Cargando...");
        // preparo el cargador para el trabajo pesado
        FontsLoader loader = new FontsLoader(dialog);
        // preparo el diálogo de carga
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        loader.execute();
        dialog.setVisible(true);
        return dialog.getResults();
    }

    private void loadFonts(List<Font> results) {
        if (results == null) {
            // cierro este diálogo cuando se haga visible
            SwingUtilities.invokeLater(this::dispose);
            return;
        }
        DefaultComboBoxModel<Font> comboBoxModel = new DefaultComboBoxModel<>();
        DefaultListModel<Font> listModel = new DefaultListModel<>();
        for (Font font : results) {
            comboBoxModel.addElement(font);
            listModel.addElement(font);
            if (font.getFamily().equals(fontList.getFont().getFamily())) {
                selectedFont = font; // asigno esta fuente que será la "seleccionada" por defecto
            }
        }
        fontFinder.setDefaults(comboBoxModel, results);
        fontList.setModel(listModel);
        fontList.setSelectedValue(selectedFont, true);
    }

}
