package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;

public class FontChooserDialog extends JDialog {

    private final JList<Font> fontJList;
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
        JTextField searchField = new JTextField();
        fontJList = new JList<>();
        fontJList.setCellRenderer(new ListFontRenderer());
        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane fontListScrollPane = new JScrollPane();
        JTextArea previewTextArea = new JTextArea("El veloz murciélago hindú comía feliz cardillo y kiwi.");
        previewTextArea.setLineWrap(true);
        previewTextArea.setWrapStyleWord(true);
        JScrollPane previewAreaScrollPane = new JScrollPane();

        previewTextArea.setText(previewTextArea.getText() + String.valueOf(System.lineSeparator()).repeat(5));

        // instalando componentes
        fontListScrollPane.getViewport().setView(fontJList);
        previewAreaScrollPane.getViewport().setView(previewTextArea);

        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        gbc.insets = new Insets(3, 3, 2, 2); // sup + inf | izq + der = 5px
        getContentPane().add(searchLabel, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        getContentPane().add(searchField, gbc);
        row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = row;
        gbc.weighty = 1.0;
        getContentPane().add(fontListScrollPane, gbc);
        row++;
        gbc.gridy = row;
        gbc.weighty = 0.5;
        getContentPane().add(previewAreaScrollPane, gbc);

        // eventos
        fontJList.addListSelectionListener(_ -> {
            Font value = fontJList.getSelectedValue();
            if (value != null) {
                previewTextArea.setFont(value);
            }
        });
        PropertyChangeListener previewListener = _ -> selectedFont = previewTextArea.getFont();
        previewTextArea.addPropertyChangeListener("font", previewListener);

        // valor de retorno
        selectedFont = getFont(); // puede retornar null CUIDADO!
    }

    public static Font createAndShow(Window owner) {
        // creo los diálogos
        FontChooserDialog dialog = new FontChooserDialog(owner);
        LoadingFontsDialog loadingFontsDialog = new LoadingFontsDialog(dialog, "Cargando...");
        // preparo el cargador para el trabajo pesado
        FontsLoader loader = new FontsLoader(loadingFontsDialog);
        loader.addPropertyChangeListener(loadingFontsDialog);
        // preparo el diálogo de carga
        loadingFontsDialog.pack();
        loadingFontsDialog.setLocationRelativeTo(dialog);
        loader.execute();
        loadingFontsDialog.setVisible(true);
        // cuando finalice recolecto los resultados obtenidos y muestro el diálogo principal
        dialog.loadFonts(loadingFontsDialog.getFontList());
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        return dialog.selectedFont;
    }

    private void loadFonts(List<Font> fontList) {
        if (fontList == null) {
            dispose(); // cierro inmediatamente este diálogo
            return;
        }
        DefaultListModel<Font> listModel = new DefaultListModel<>();
        for (Font font : fontList) {
            listModel.addElement(font);
            if (font.getFamily().equals(fontJList.getFont().getFamily())) {
                selectedFont = font; // asigno esta fuente que será la "seleccionada" por defecto
            }
        }
        fontJList.setModel(listModel);
        fontJList.setSelectedValue(selectedFont, true);
    }

}
