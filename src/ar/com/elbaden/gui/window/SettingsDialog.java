package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static javax.swing.LayoutStyle.*;
import static javax.swing.GroupLayout.*;

public class SettingsDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class.getName());

    private final Properties changes;

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

        DefaultComboBoxModel<Font> familyModel = new DefaultComboBoxModel<>();
        JLabel fontLabel = new JLabel(App.messages.getString("fontFamily"));
        familyModel.addElement(getFont());
        JComboBox<Font> familyCombo = new JComboBox<>(familyModel);
        fontLabel.setLabelFor(familyCombo);
        familyCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Font font && c instanceof JLabel label) {
                    label.setText(font.getFamily());
                }
                return c;
            }
        });
        JLabel sizeLabel = new JLabel(App.messages.getString("size"));
        SpinnerNumberModel sizeModel = new SpinnerNumberModel(10, 10, 24, 1);
        JSpinner sizeSpinner = new JSpinner(sizeModel);
        sizeLabel.setLabelFor(sizeSpinner);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setMaximumSize(new Dimension(640, 480));

        JButton okBtn = new JButton(ok);
        okBtn.setActionCommand(ok);
        JButton cancelBtn = new JButton(cancel);
        cancelBtn.setActionCommand(cancel);
        JButton applyBtn = new JButton(apply);
        applyBtn.setActionCommand(apply);
        applyBtn.setEnabled(false);

        // instalando componentes
        generalPanel.add(askToExitBtn);
        settingsBoxPanel.add(generalPanel);
        fontPanel.add(fontLabel);
        fontPanel.add(familyCombo);
        fontPanel.add(sizeLabel);
        fontPanel.add(sizeSpinner);
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
        askToExitBtn.addActionListener(notifyChange(applyBtn));
        okBtn.addActionListener(_ -> {
            saveChanges(applyBtn);
            dispose();
        });
        cancelBtn.addActionListener(_ -> dispose());
        applyBtn.addActionListener(_ -> saveChanges(applyBtn));
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

    private void saveChanges(AbstractButton applyButton) {
        Map<Object, Object> copy = Map.copyOf(App.settings);
        changes.forEach((key, value) -> {
            if (App.settings.containsKey(key)) {
                App.settings.put(key, value);
            }
        });
        try {
            App.settings.save();
            changes.clear();
            applyButton.setEnabled(false);
        } catch (IOException e) {
            App.settings.putAll(copy);
            LOGGER.severe(e.getMessage());
            ErrorDialog.createAndShow(this, e);
        }
    }

    private ActionListener notifyChange(AbstractButton applyBtn) {
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
