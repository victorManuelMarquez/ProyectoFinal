package ar.com.elbaden.main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
        private List<String> list;

        public Editor(JComboBox<String> comboBox, String[] fontFamilies) {
            this.comboBox = comboBox;
            this.fontFamilies = fontFamilies;
            defaultModel = comboBox.getModel();
            editingValue = fontFamilies[0];
            list = List.of();
            getDocument().addDocumentListener(this);
        }

        @Override
        public Component getEditorComponent() {
            return this;
        }

        @Override
        public void setItem(Object anObject) {}

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
                if (list.isEmpty()) {
                    return;
                }
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
                list = List.of();
                editingValue = defaultModel.getElementAt(0);
                comboBox.setModel(defaultModel);
                return;
            }
            list = new ArrayList<>();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            String regex = "(?i).*" + Pattern.quote(value) + ".*";
            Pattern pattern = Pattern.compile(regex);
            for (String family : fontFamilies) {
                if (pattern.matcher(family).find()) {
                    list.add(family);
                    model.addElement(family);
                }
            }
            editingValue = model.getElementAt(0);
            comboBox.setModel(model);
            comboBox.showPopup();
        }

    }

}
