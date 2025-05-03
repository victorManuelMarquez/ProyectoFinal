package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

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

    public void setDefaults(List<Font> fontList) {
        fontEditor.setFontList(fontList);
    }

    static class FontEditor extends JTextField
                            implements ComboBoxEditor, DocumentListener, ItemListener, ActionListener {

        private final JComboBox<Font> comboBox;
        private List<Font> fontList;
        private Object actualItem;
        private boolean isAutomaticUpdate = false;
        private int caretPosition = 0;
        private boolean isPartialContent = false;

        public FontEditor(JComboBox<Font> comboBox) {
            this.comboBox = comboBox;
            getDocument().addDocumentListener(this);
        }

        @Override
        public void setText(String t) {
            isAutomaticUpdate = true;
            super.setText(t);
            isPartialContent = false;
            isAutomaticUpdate = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("comboBoxEdited".equals(e.getActionCommand())) {
                if (isPartialContent && actualItem instanceof Font font) {
                    String family = font.getFamily();
                    if (family.toLowerCase().startsWith(getText().toLowerCase())) {
                        setText(family);
                    }
                }
                caretPosition = 0;
                selectAll();
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem() instanceof Font font) {
                    setText(font.getFamily());
                }
            }
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
            caretPosition = (e.getLength() > 1) ? getCaretPosition() : getCaretPosition() + 1;
            search(getText());
            SwingUtilities.invokeLater(() -> {
                if (getComboBox().getModel().getSelectedItem() instanceof Font font) {
                    setText(font.getFamily());
                    select(caretPosition, font.getFamily().length());
                }
            });
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (isAutomaticUpdate) {
                return;
            }
            caretPosition = (e.getLength() > 1) ? getCaretPosition() : getCaretPosition() - 1;
            try {
                Document document = e.getDocument();
                String content = document.getText(0, document.getLength());
                search(content);
                isPartialContent = true;
            } catch (BadLocationException ex) {
                ex.printStackTrace(System.err);
            }
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
