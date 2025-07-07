package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.*;

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
        JLabel messageLabel = new JLabel(App.messages.getString("closingMessage"));
        JCheckBox confirmExitBtn = new JCheckBox(App.messages.getString("confirmExit"));
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
            Properties properties = App.properties;
            File propertiesFile = new File(Settings.getAppFolder(), "settings.properties");
            try (FileOutputStream outputStream = new FileOutputStream(propertiesFile)) {
                properties.store(outputStream, "settings");
                App.properties.setProperty("settings.showClosingDialog.value", Boolean.toString(!checked));
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
        String key = "settings.showClosingDialog.value";
        if (App.properties.containsKey(key)) {
            String bool = App.properties.getProperty(key);
            if (!Boolean.parseBoolean(bool)) {
                return JOptionPane.OK_OPTION;
            }
        }
        try {
            ClosingDialog dialog = new ClosingDialog(owner, App.messages.getString("attention"));
            Settings.updateFont(dialog);
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
