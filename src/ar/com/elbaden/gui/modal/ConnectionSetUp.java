package ar.com.elbaden.gui.modal;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.input.FilteredPasswordField;
import ar.com.elbaden.gui.input.FilteredTextField;
import ar.com.elbaden.gui.prefab.ConnectionForm;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ConnectionSetUp extends MasterDialog {

    private boolean success;

    private ConnectionSetUp(Window owner, String title) {
        super(owner, title);
        setResizable(false);
        ResourceBundle messages;
        try {
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new RuntimeException(e);
        }
        // contenido local
        String localApply  = messages.getString("button.apply");
        String localCancel = messages.getString("button.cancel");

        // componentes
        ConnectionForm connectionForm = new ConnectionForm(false);

        JButton applyButton = new JButton(localApply);

        JButton cancelButton = new JButton(localCancel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.add(applyButton);
        buttonsPanel.add(cancelButton);

        // instalando los componentes en el dialog
        getContentPane().add(connectionForm);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // eventos
        FilteredTextField userField = connectionForm.getUserField();
        FilteredPasswordField passwordField = connectionForm.getPasswordField();

        applyButton.addActionListener(_ -> {
            if (userField.needRevision()) {
                userField.requestFocusInWindow();
                userField.showMinimumNotMet();
                return;
            }
            if (passwordField.needRevision()) {
                passwordField.requestFocusInWindow();
                passwordField.showMinimumNotMet();
                return;
            }
            String user = userField.getText();
            String pass = new String(passwordField.getPassword());
            Window root = SwingUtilities.windowForComponent(applyButton);
            App.settings.getProperties().setProperty(Settings.KEY_USERNAME_DB, user);
            App.settings.getProperties().setProperty(Settings.KEY_PASSWORD_DB, pass);
            success = DataBank.testConnection(root);
            if (success) {
                String comments = messages.getString("ini.comments");
                App.settings.applyChanges(root, comments);
            } else {
                App.settings.discardChanges();
            }
            dispose();
        });

        cancelButton.addActionListener(_ -> dispose());
    }

    public static boolean createAndShow(Window owner) {
        try {
            ResourceBundle locale = ResourceBundle.getBundle(App.LOCALES_DIR);
            ConnectionSetUp setUp = new ConnectionSetUp(owner, locale.getString("connection_setup.title"));
            setUp.pack();
            setUp.setLocationRelativeTo(owner);
            setUp.setVisible(true);
            return setUp.success;
        } catch (MissingResourceException e) {
            throw new RuntimeException(e);
        }
    }

}
