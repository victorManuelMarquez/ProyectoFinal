package ar.com.elbaden.gui.window;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

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

        JButton okBtn = new JButton(ok);
        okBtn.setActionCommand(ok);
        JButton cancelBtn = new JButton(cancel);
        cancelBtn.setActionCommand(cancel);
        JButton applyBtn = new JButton(apply);
        applyBtn.setActionCommand(apply);

        // instalando componentes
        getRootPane().setDefaultButton(okBtn);
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addComponent(okBtn)
                .addComponent(cancelBtn)
                .addComponent(applyBtn));
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(okBtn)
                        .addComponent(cancelBtn)
                        .addComponent(applyBtn)));
        getContentPane().add(mainPanel);

        // eventos
        okBtn.addActionListener(_ -> dispose());
        cancelBtn.addActionListener(_ -> dispose());
        applyBtn.addActionListener(_ -> dispose());
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
            LOGGER.severe(e.getMessage());
        }
    }

}
