package ar.com.elbaden.gui.window;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.logging.Logger;

import static javax.swing.LayoutStyle.*;
import static javax.swing.GroupLayout.*;

public class SettingsDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class.getName());

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private SettingsDialog(Window owner, String title) {
        super(owner, title);
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

        String generalPaneName = App.messages.getString("settingsDialog.generalPanel.name");
        JPanel generalPanel = new JPanel();
        generalPanel.setName(generalPaneName);
        installTitledBorder(generalPanel);

        String askToExit = App.messages.getString("settingsDialog.askToExit");
        JCheckBox askToExitBtn = new JCheckBox(askToExit);

        JButton okBtn = new JButton(ok);
        okBtn.setActionCommand(ok);
        JButton cancelBtn = new JButton(cancel);
        cancelBtn.setActionCommand(cancel);
        JButton applyBtn = new JButton(apply);
        applyBtn.setActionCommand(apply);

        // instalando componentes
        getRootPane().setDefaultButton(okBtn);
        generalPanel.add(askToExitBtn);
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(generalPanel)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.RELATED, PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(okBtn)
                                .addComponent(cancelBtn)
                                .addComponent(applyBtn))));
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(generalPanel))
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(okBtn)
                        .addComponent(cancelBtn)
                        .addComponent(applyBtn)));
        getContentPane().add(mainPanel);

        // eventos
        okBtn.addActionListener(_ -> dispose());
        cancelBtn.addActionListener(_ -> dispose());
        applyBtn.addActionListener(_ -> dispose());
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

    public static void createAndShow(Window owner) {
        try {
            String title = App.messages.getString("settingsDialog.title");
            SettingsDialog dialog = new SettingsDialog(owner, title);
            App.settings.updateFonts(dialog);
            dialog.pack();
            dialog.setLocationRelativeTo(owner);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            LOGGER.severe(e.getMessage());
        }
    }

}
