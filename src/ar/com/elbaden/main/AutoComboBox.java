package ar.com.elbaden.main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

public class AutoComboBox {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            JFrame frame = new JFrame("Demo ComboBox con autocompletado");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JComboBox<String> comboBox = new JComboBox<>(families);
            Editor editor = new Editor(comboBox, families);
            comboBox.setEditable(true);
            comboBox.setEditor(editor);
            frame.add(comboBox);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            editor.getDocument().addDocumentListener(editor); // debo mejorar esta lógica que quedo rara
            comboBox.addItemListener(editor);
            comboBox.addActionListener(editor);
        });
    }

    static class Editor extends JTextField
            implements ComboBoxEditor, DocumentListener, ItemListener, ActionListener {

        private final JComboBox<String> comboBox;
        private final ComboBoxModel<String> defaultModel;
        private final String[] fontFamilies;
        private String editingValue;
        private String typedValue;
        private String selectedValue;
        private boolean searchDisabled = false;
        private int caretPosition = 0;

        public Editor(JComboBox<String> comboBox, String[] fontFamilies) {
            this.comboBox = comboBox;
            this.fontFamilies = fontFamilies;
            defaultModel = comboBox.getModel();
            editingValue = fontFamilies[0];
        }

        @Override
        public Component getEditorComponent() {
            return this;
        }

        @Override
        public void setItem(Object anObject) {
            // deja esto vacío o se arruina toda la estrategía
        }

        @Override
        public Object getItem() {
            return getText();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (searchDisabled) {
                return;
            }
            caretPosition = getCaretPosition() + 1;
            search(getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (searchDisabled) {
                return;
            }
            caretPosition = getCaretPosition() - 1;
            search(getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            searchDisabled = true;
            selectedValue = e.getItem().toString();
            setText(selectedValue);
            if (getText().startsWith(typedValue == null ? "" : typedValue)) {
                select(caretPosition, selectedValue.length());
            }
            searchDisabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("comboBoxEdited".equals(e.getActionCommand())) {
                List<String> list = Arrays.asList(fontFamilies);
                if (!list.contains(selectedValue)) {
                    searchDisabled = true;
                    setText(editingValue);
                    searchDisabled = false;
                }
            }
        }

        public void search(String value) {
            typedValue = value;
            if (value.isBlank()) {
                editingValue = defaultModel.getElementAt(0);
                comboBox.setModel(defaultModel);
                return;
            }
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (String family : fontFamilies) {
                if (family.matches("(?i).*" + value + ".*")) {
                    model.addElement(family);
                }
            }
            editingValue = model.getElementAt(0);
            comboBox.setModel(model);
            comboBox.showPopup();
        }

    }

}
