package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class ClosingDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(ClosingDialog.class.getName());

    private int exitOption = JOptionPane.DEFAULT_OPTION;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private ClosingDialog(Window owner, String title) {
        super(owner, title);
        // componentes
        JPanel mainPanel = new JPanel(null);
        GroupLayout groupLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(groupLayout);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        JLabel messageLabel = new JLabel(App.messages.getString("closingDialog.message"));
        JCheckBox confirmExitBtn = new JCheckBox(App.messages.getString("closingDialog.confirmExit"));
        JButton exitBtn = new JButton(App.messages.getString("exit"));
        JButton cancelBtn = new JButton(App.messages.getString("cancel"));

        // instalando componentes
        getRootPane().setDefaultButton(exitBtn);
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addComponent(iconLabel)
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(messageLabel)
                        .addComponent(confirmExitBtn)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.UNRELATED, PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(exitBtn)))
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(cancelBtn)));
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(iconLabel)
                        .addComponent(messageLabel))
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(confirmExitBtn))
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(exitBtn)
                        .addComponent(cancelBtn)));
        getContentPane().add(mainPanel);

        // eventos
        SwingUtilities.invokeLater(exitBtn::requestFocusInWindow);
        confirmExitBtn.addActionListener(_ -> {
            boolean checked = confirmExitBtn.isSelected();
            Settings settings = App.settings;
            settings.setProperty("settings.showClosingDialog", Boolean.toString(!checked));
            try {
                String output = settings.save();
                LOGGER.info(output);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        });
        exitBtn.addActionListener(_ -> {
            exitOption = JOptionPane.OK_OPTION;
            dispose();
        });
        cancelBtn.addActionListener(_ -> {
            exitOption = JOptionPane.CANCEL_OPTION;
            dispose();
        });
    }

    public static int createAndShow(Window owner) {
        try {
            ClosingDialog dialog = new ClosingDialog(owner, App.messages.getString("closingDialog.title"));
            MnemonicFinder.automaticMnemonics(dialog);
            dialog.pack();
            dialog.setLocationRelativeTo(owner);
            dialog.setVisible(true);
            return dialog.exitOption;
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
        return JOptionPane.OK_OPTION;
    }

}
