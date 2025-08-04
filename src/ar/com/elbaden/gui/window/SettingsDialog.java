package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.gui.component.FontFamilyComboBoxModel;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import static javax.swing.GroupLayout.Alignment;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement;

public class SettingsDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class.getName());

    private final Properties changes;
    private final AbstractButton applyBtn;
    private final Map<String, List<String>> familyMap;
    private String actualFamily;
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
        FontFamilyComboBoxModel familyComboBoxModel = new FontFamilyComboBoxModel();
        familyMap = familyComboBoxModel.getFamilyMap();
        actualFamily = familyComboBoxModel.getGeneralFontFamily();
        JComboBox<String> familyCombo = new JComboBox<>(familyComboBoxModel);
        fontLabel.setLabelFor(familyCombo);
        JPanel familyGroupPanel = new JPanel(null);
        installFamilyGroupsCombos(familyGroupPanel);
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
        int actualFontSize = App.settings.generalFontSize();
        if (!sizes.contains(actualFontSize)) {
            sizeCombo.addItem(actualFontSize);
        }
        sizeCombo.setSelectedItem(actualFontSize);
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
        fontPanel.add(familyGroupPanel);
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
        familyCombo.addItemListener(notifyFamilyChange());
        familyCombo.addActionListener(_ -> {
            LayoutManager layoutManager = familyGroupPanel.getLayout();
            if (layoutManager instanceof CardLayout cardLayout) {
                Object selectedItem = familyCombo.getSelectedItem();
                if (selectedItem != null) {
                    cardLayout.show(familyGroupPanel, selectedItem.toString());
                }
            }
        });
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

    private void installFamilyGroupsCombos(Container container) {
        CardLayout layout = new CardLayout();
        container.setLayout(layout);
        familyMap.forEach((k, v) -> {
            if (!v.isEmpty()) {
                JComboBox<String> familyCombo = new JComboBox<>(new Vector<>(v));
                familyCombo.setSelectedItem(actualFamily);
                familyCombo.addItemListener(notifyFamilyChange());
                familyCombo.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        Object item = familyCombo.getSelectedItem();
                        if (actualFamily.equals(item)) {
                            return;
                        }
                        int index = familyCombo.getSelectedIndex();
                        ItemEvent itemEvent = new ItemEvent(familyCombo, index, item, ItemEvent.SELECTED);
                        notifyFamilyChange().itemStateChanged(itemEvent);
                    }
                });
                container.add(familyCombo, k);
                if (v.contains(actualFamily)) {
                    layout.show(container, k);
                }
            } else {
                container.add(new JLabel(), k);
            }
        });
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
            // actualizo el valor
            actualFamily = App.settings.generalFontFamily();
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

    private ItemListener notifyFamilyChange() {
        return evt -> {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                if (evt.getItem() instanceof String item) {
                    int totalUpdates = 0;
                    // sí es una sola fuente
                    boolean isKey = familyMap.containsKey(item) && familyMap.get(item).isEmpty();
                    // si es parte de una familia
                    boolean isValue = familyMap.keySet().stream().anyMatch(k -> familyMap.get(k).contains(item));
                    // lo establezco como fuente general
                    if (isKey || isValue) {
                        for (String key : App.settings.fontFamilyKeys()) {
                            changes.put(key, item);
                            totalUpdates++;
                        }
                    }
                    fontUpdated = totalUpdates > 0;
                    applyBtn.setEnabled(!changes.isEmpty());
                }
            }
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
