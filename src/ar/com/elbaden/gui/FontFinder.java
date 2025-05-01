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

@Deprecated
public class FontFinder extends JComboBox<Font> {

    private final FontEditor fontEditor;

    public FontFinder() {
        setRenderer(new FontCellRenderer());
        setEditable(true);
        this.fontEditor = new FontEditor(this);
        setEditor(fontEditor);
        addItemListener(fontEditor);
        addActionListener(fontEditor);
    }

    public void setDefaults(ComboBoxModel<Font> defaultModel, List<Font> fontList) {
        fontEditor.setFontList(fontList);
    }

    static class FontEditor extends JTextField
                            implements ComboBoxEditor, DocumentListener, ItemListener, ActionListener {

        private final JComboBox<Font> comboBox;
        private List<Font> fontList;
        private Object actualItem;
        private boolean isAutomaticUpdate = false;

        public FontEditor(JComboBox<Font> comboBox) {
            this.comboBox = comboBox;
            getDocument().addDocumentListener(this);
        }

        @Override
        public void setText(String t) {
            isAutomaticUpdate = true;
            super.setText(t);
            isAutomaticUpdate = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
        }

        @Override
        public Component getEditorComponent() {
            return this;
        }

        @Override
        public void setItem(Object anObject) {
            actualItem = anObject;
        }

        @Override
        public Object getItem() {
            return actualItem;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (isAutomaticUpdate) {
                return;
            }
            search(getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (isAutomaticUpdate) {
                return;
            }
            search(getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}

        private void search(String value) {
            if (value.isBlank()) {
                getComboBox().setModel(new DefaultComboBoxModel<>());
                return;
            }
            DefaultComboBoxModel<Font> model = new DefaultComboBoxModel<>();
            for (Font font : getFontList()) {
                if (font.getFamily().startsWith(value)) {
                    model.addElement(font);
                }
            }
            getComboBox().setModel(model);
            getComboBox().showPopup();
        }

        public JComboBox<Font> getComboBox() {
            return comboBox;
        }

        public List<Font> getFontList() {
            return fontList;
        }

        public void setFontList(List<Font> fontList) {
            this.fontList = fontList;
        }

    }

}
