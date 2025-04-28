package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.regex.Pattern;

public class FontFinder extends JComboBox<Font> {

    private final FontEditor fontEditor;

    public FontFinder() {
        setRenderer(new ListFontRenderer());
        setEditable(true);
        this.fontEditor = new FontEditor(this);
        setEditor(fontEditor);
        addItemListener(fontEditor);
        addActionListener(fontEditor);
    }

    public void setDefaults(ComboBoxModel<Font> defaultModel, List<Font> fontList) {
        setModel(defaultModel);
        fontEditor.setDefaultModel(defaultModel);
        fontEditor.setFontList(fontList);
    }

    static class FontEditor extends JTextField
                            implements ComboBoxEditor, DocumentListener, ItemListener, ActionListener {

        private final JComboBox<Font> comboBox;
        private ComboBoxModel<Font> defaultModel;
        private List<Font> fontList;
        private boolean isAutoInput = false;
        private Font selectedFont;
        private int caretPosition;
        private String previous;

        public FontEditor(JComboBox<Font> comboBox) {
            this.comboBox = comboBox;
            getDocument().addDocumentListener(this);
        }

        @Override
        public void setText(String t) {
            isAutoInput = true;
            super.setText(t);
            isAutoInput = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("comboBoxEdited".equals(e.getActionCommand())) {
                int size = getComboBox().getModel().getSize();
                if (!getText().isBlank() && selectedFont == null && size > 0) {
                    selectedFont = getComboBox().getItemAt(0);
                    setText(selectedFont.getFamily());
                    selectAll();
                    return;
                }
                // en esta acción ya no es necesario una selección parcial
                if (getSelectionStart() > 0) {
                    selectAll();
                }
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getItem() instanceof Font font) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedFont = font;
                    setText(selectedFont.getFamily());
                    if (previous == null) {
                        selectAll();
                        return;
                    }
                    if (getText().toLowerCase().startsWith(previous.toLowerCase())) {
                        select(caretPosition, getText().length());
                    }
                }
            }
        }

        @Override
        public Component getEditorComponent() {
            return this;
        }

        @Override
        public void setItem(Object anObject) {}

        @Override
        public Object getItem() {
            return selectedFont;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (isAutoInput) {
                return;
            }
            caretPosition = (e.getLength() > 1) ? getCaretPosition() : getCaretPosition() + 1;
            search();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (isAutoInput) {
                return;
            }
            caretPosition = (e.getLength() > 1) ? getCaretPosition() : getCaretPosition() - 1;
            search();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}

        public void resetField() {
            getComboBox().setModel(getDefaultModel());
            setText("");
            previous = getText();
            caretPosition = 0;
            selectedFont = null;
        }

        private void search() {
            if (getText().isBlank()) {
                resetField();
                return;
            }
            selectedFont = null;
            previous = getText();
            String regex = "(?i).*" + Pattern.quote(getText()) + ".*";
            Pattern pattern = Pattern.compile(regex);
            DefaultComboBoxModel<Font> newModel = new DefaultComboBoxModel<>();
            for (Font font : getFontList()) {
                if (pattern.matcher(font.getFamily()).find()) {
                    newModel.addElement(font);
                }
            }
            getComboBox().setModel(newModel);
            getComboBox().showPopup();
        }

        public JComboBox<Font> getComboBox() {
            return comboBox;
        }

        public ComboBoxModel<Font> getDefaultModel() {
            return defaultModel;
        }

        public void setDefaultModel(ComboBoxModel<Font> defaultModel) {
            this.defaultModel = defaultModel;
        }

        public List<Font> getFontList() {
            return fontList;
        }

        public void setFontList(List<Font> fontList) {
            this.fontList = fontList;
        }

    }

}
