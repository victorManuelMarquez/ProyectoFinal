package ar.com.elbaden.main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class AutoComboBox {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            JFrame frame = new JFrame("AutoCombo");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            DefaultComboBoxModel<String> defaultModel = new DefaultComboBoxModel<>(families);
            JComboBox<String> comboBox = new JComboBox<>(defaultModel);
            SearchComboEditor editor = new SearchComboEditor(comboBox, families);
            comboBox.setEditable(true);
            comboBox.setEditor(editor);
            frame.add(comboBox);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            editor.getDocument().addDocumentListener(editor);
            comboBox.addActionListener(editor);
            comboBox.addItemListener(editor);
        });
    }

    static class SearchComboEditor extends JTextField
            implements ComboBoxEditor, DocumentListener, ActionListener, ItemListener {

        private final JComboBox<String> comboBox;
        private final ComboBoxModel<String> defaultModel;
        private final String[] families;
        private boolean scrolling = false;
        private int caretPosition = 0;
        private String previousValue;

        public SearchComboEditor(JComboBox<String> comboBox, String[] families) {
            this.comboBox = comboBox;
            this.families = families;
            this.defaultModel = comboBox.getModel();
        }

        @Override
        public Component getEditorComponent() {
            return this;
        }

        @Override
        public void setItem(Object anObject) {}

        @Override
        public Object getItem() {
            System.out.println("getItem: " + getText());
            return getText();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (scrolling) {
                return;
            }
            caretPosition = getCaretPosition() + 1;
            search(getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (scrolling) {
                return;
            }
            caretPosition = getCaretPosition() - 1;
            search(getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            search(getText());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("comboBoxEdited".equals(e.getActionCommand())) {
                setCaretPosition(getText().length());
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            scrolling = true;
            String selectedValue = e.getItem().toString();
            setText(selectedValue);
            if (getText().startsWith(previousValue)) {
                select(caretPosition, selectedValue.length());
            }
            scrolling = false;
        }

        public void search(String value) {
            previousValue = value;
            if (value.isBlank()) {
                comboBox.setModel(defaultModel);
                return;
            }
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (String family : families) {
                if (family.matches("(?i).*" + value + ".*")) {
                    model.addElement(family);
                }
            }
            comboBox.setModel(model);
            comboBox.showPopup();
        }

    }

}
