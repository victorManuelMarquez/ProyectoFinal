package ar.com.elbaden.gui.modal;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ConnectionSetUp extends MasterDialog {

    private boolean success;

    private ConnectionSetUp(Window owner, String title) {
        super(owner, title);
        setLayout(new GridBagLayout());
        setResizable(false);
        ResourceBundle messages;
        try {
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new RuntimeException(e);
        }
        // contenido local
        String localUser = messages.getString("label.user_database");
        String localPass = messages.getString("label.password_database");
        String localShow = messages.getString("button.show");
        String localHide = messages.getString("button.hide");
        String localApply  = messages.getString("button.apply");
        String localCancel = messages.getString("button.cancel");

        // componentes
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.weightx = 1.0;

        Insets textMargins = UIManager.getInsets("TextPane.margin");

        JLabel userLabel = new JLabel(localUser);

        JLabel passLabel = new JLabel(localPass);

        JTextField userField = new JTextField();
        userField.setMargin(textMargins);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMargin(textMargins);
        passwordField.setColumns(12);
        char defaultEcho = passwordField.getEchoChar();

        JButton showPassBtn = new JButton(localShow);

        JButton applyButton = new JButton(localApply);

        JButton cancelButton = new JButton(localCancel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.add(applyButton);
        buttonsPanel.add(cancelButton);

        // instalando los componentes en el dialog
        int row = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(userLabel, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(userField, constraints);

        row++;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = 1;
        constraints.gridy = row;
        add(passLabel, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        add(passwordField, constraints);

        add(showPassBtn, constraints);

        row++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridy = row;
        add(buttonsPanel, constraints);

        // eventos
        showPassBtn.addActionListener(_ -> {
            if (passwordField.getEchoChar() == defaultEcho) {
                passwordField.setEchoChar(Character.MIN_VALUE);
                showPassBtn.setText(localHide);
            } else {
                passwordField.setEchoChar(defaultEcho);
                showPassBtn.setText(localShow);
            }
        });

        applyButton.addActionListener(_ -> {
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
