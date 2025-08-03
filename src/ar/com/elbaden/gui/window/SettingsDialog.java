package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.gui.component.FontFamilyComboBoxModel;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import static javax.swing.GroupLayout.Alignment;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement;

public class SettingsDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class.getName());

    private final Properties changes;
    private final AbstractButton applyBtn;
    private boolean fontUpdated;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private SettingsDialog(Window owner, String title) {
        super(owner, title);
        changes = new Properties();

        // comandos
        String ok = App.messages.getString("ok");
        String cancel = App.messages.getString("cancel");
        String apply = App.messages.getString("apply");

        // componentes
        JPanel mainPanel = new JPanel(null);
        GroupLayout groupLayout = new GroupLayout(mainPanel);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);
        mainPanel.setLayout(groupLayout);

        Box settingsBoxPanel = Box.createVerticalBox();

        String generalPaneName = App.messages.getString("settingsDialog.generalPanel.name");
        JPanel generalPanel = new JPanel();
        generalPanel.setName(generalPaneName);
        installTitledBorder(generalPanel);

        String askToExit = App.messages.getString("settingsDialog.askToExit");
        JCheckBox askToExitBtn = new JCheckBox(askToExit);
        String closingDialogKey = "settings.showClosingDialog";
        askToExitBtn.setActionCommand(closingDialogKey);
        String confirmValue = App.settings.getProperty(closingDialogKey);
        askToExitBtn.setSelected(Boolean.parseBoolean(confirmValue));

        String fontPaneName = App.messages.getString("settingsDialog.fontPanel.name");
        JPanel fontPanel = new JPanel();
        fontPanel.setName(fontPaneName);
        installTitledBorder(fontPanel);

        JLabel fontLabel = new JLabel(App.messages.getString("fontFamily"));
        JComboBox<String> familyCombo = new JComboBox<>(new FontFamilyComboBoxModel());
        fontLabel.setLabelFor(familyCombo);
        JLabel sizeLabel = new JLabel(App.messages.getString("size"));
        Vector<Integer> sizes = new Vector<>(List.of(8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24));
        DefaultComboBoxModel<Integer> sizeModel = new DefaultComboBoxModel<>(sizes);
        JComboBox<Integer> sizeCombo = new JComboBox<>(sizeModel);
        sizeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                Component c;
                c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel label && value instanceof Number number) {
                    if (number.intValue() == Settings.DEFAULT) {
                        label.setText(App.messages.getString("default"));
                    } else if (number.intValue() == Settings.CUSTOM) {
                        label.setText(App.messages.getString("custom"));
                    }
                }
                return c;
            }
        });
        int generalFontSize = App.settings.generalFontSize();
        if (!sizes.contains(generalFontSize)) {
            sizeCombo.addItem(generalFontSize);
        }
        sizeCombo.setSelectedItem(generalFontSize);
        sizeLabel.setLabelFor(sizeCombo);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setMaximumSize(new Dimension(640, 480));

        JButton okBtn = new JButton(ok);
        okBtn.setActionCommand(ok);
        JButton cancelBtn = new JButton(cancel);
        cancelBtn.setActionCommand(cancel);
        applyBtn = new JButton(apply);
        applyBtn.setActionCommand(apply);
        applyBtn.setEnabled(false);

        // instalando componentes
        generalPanel.add(askToExitBtn);
        settingsBoxPanel.add(generalPanel);
        fontPanel.add(fontLabel);
        fontPanel.add(familyCombo);
        fontPanel.add(sizeLabel);
        fontPanel.add(sizeCombo);
        settingsBoxPanel.add(fontPanel);
        scrollPane.setViewportView(settingsBoxPanel);
        getRootPane().setDefaultButton(okBtn);
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(scrollPane)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.RELATED, PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(okBtn)
                                .addComponent(cancelBtn)
                                .addComponent(applyBtn))));
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(scrollPane))
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(okBtn)
                        .addComponent(cancelBtn)
                        .addComponent(applyBtn)));
        getContentPane().add(mainPanel);

        // eventos
        askToExitBtn.addActionListener(notifyShowClosingDialogChanged());
        familyCombo.addActionListener(notifyFamilyChange());
        sizeCombo.addItemListener(notifySizeChange());
        okBtn.addActionListener(_ -> {
            saveChanges();
            dispose();
        });
        cancelBtn.addActionListener(_ -> dispose());
        applyBtn.addActionListener(_ -> saveChanges());
    }

    private void installTitledBorder(JComponent component) {
        if (component == null || component instanceof JLabel || component instanceof AbstractButton) {
            return;
        }
        if (component.getName() == null || component.getName().isBlank()) {
            return;
        }
        Border titledBorder = BorderFactory.createTitledBorder(component.getName());
        component.setBorder(titledBorder);
    }

    private void saveChanges() {
        Map<Object, Object> copy = Map.copyOf(App.settings);
        changes.forEach((key, value) -> {
            if (App.settings.containsKey(key)) {
                App.settings.put(key, value);
            }
        });
        try {
            App.settings.save();
            changes.clear();
            applyBtn.setEnabled(false);
            if (fontUpdated) {
                App.settings.updateFonts(getOwner());
                App.settings.updateFonts(this);
            }
        } catch (IOException e) {
            App.settings.putAll(copy);
            LOGGER.severe(e.getMessage());
            ErrorDialog.createAndShow(this, e);
        }
    }

    private ActionListener notifyShowClosingDialogChanged() {
        return event -> {
            String key = event.getActionCommand();
            if (key != null && App.settings.containsKey(key)) {
                Object source = event.getSource();
                if (source instanceof JToggleButton button) {
                    String value = Boolean.toString(button.isSelected());
                    String actualValue = App.settings.getProperty(key);
                    if (value.equals(actualValue)) {
                        changes.remove(key);
                    } else {
                        changes.setProperty(key, value);
                    }
                }
                applyBtn.setEnabled(!changes.isEmpty());
            }
        };
    }

    private ActionListener notifyFamilyChange() {
        return evt -> {
            int totalUpdates = 0;
            if (evt.getSource() instanceof JComboBox<?> comboBox) {
                if (comboBox.getModel() instanceof FontFamilyComboBoxModel model) {
                    Map<String, List<String>> familyMap = model.getFamilyMap();
                    Object selectedItem = comboBox.getSelectedItem();
                    if (selectedItem == null) {
                        return;
                    }
                    String fontFamilySelected = selectedItem.toString();
                    if (familyMap.containsKey(fontFamilySelected)) {
                        List<String> list = familyMap.get(fontFamilySelected);
                        // sí es una única fuente
                        if (list.isEmpty()) {
                            for (String key : App.settings.fontFamilyKeys()) {
                                changes.put(key, fontFamilySelected);
                                totalUpdates++;
                            }
                        } // queda pendiente el otro caso por qué no están todos los componentes necesarios.
                    }
                }
            }
            fontUpdated = totalUpdates > 0;
            applyBtn.setEnabled(!changes.isEmpty());
        };
    }

    private ItemListener notifySizeChange() {
        return evt -> {
            int totalUpdates = 0;
            if (evt.getItem() != null && evt.getStateChange() == ItemEvent.SELECTED) {
                Object item = evt.getItem();
                if (item instanceof Number number) {
                    // si es un tamaño de fuente general
                    if (number.intValue() > 0) {
                        for (String key : App.settings.fontSizeKeys()) {
                            Object value = App.settings.get(key);
                            if (!value.equals(item)) {
                                changes.put(key, item.toString());
                                totalUpdates++;
                            }
                        }
                    }
                }
            }
            fontUpdated = totalUpdates > 0;
            applyBtn.setEnabled(!changes.isEmpty());
        };
    }

    public static void createAndShow(Window owner) {
        try {
            String title = App.messages.getString("settingsDialog.title");
            SettingsDialog dialog = new SettingsDialog(owner, title);
            App.settings.updateFonts(dialog);
            MnemonicFinder.automaticMnemonics(dialog);
            dialog.pack();
            dialog.setLocationRelativeTo(owner);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            LOGGER.severe(e.getMessage());
        }
    }

}
