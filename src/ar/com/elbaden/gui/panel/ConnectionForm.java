package ar.com.elbaden.gui.panel;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.button.AdaptableButton;
import ar.com.elbaden.gui.field.FieldMargin;
import ar.com.elbaden.gui.field.FilteredPasswordField;
import ar.com.elbaden.gui.field.FilteredTextField;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ConnectionForm extends JPanel implements ActionListener {

    private final FilteredTextField userField;
    private final FilteredPasswordField passwordField;

    private boolean success = false;

    public ConnectionForm(Boolean isModule) throws MissingResourceException {
        super(new GridBagLayout());

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localUser = messages.getString("label.user_database");
        String localPass = messages.getString("label.password_database");
        String localShow = messages.getString("button.show");
        String localHide = messages.getString("button.hide");

        setName(messages.getString("literal.connection"));

        // variables
        final int space = 8, margin = 4;
        int row = 0;

        // componentes
        Border emptyBorder = BorderFactory.createEmptyBorder(space, space, space, space);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(margin, margin, margin, margin);
        constraints.weightx = 1.0;

        JLabel userLabel = new JLabel(localUser);

        JLabel passLabel = new JLabel(localPass);

        userField = new FilteredTextField("^(?!\\d)\\w+$", 4, 16);
        userField.setMargin(new FieldMargin());
        userField.setName(userLabel.getText());

        passwordField = new FilteredPasswordField("^\\w+$", 8, 16);
        passwordField.setColumns(12);
        passwordField.setMargin(new FieldMargin());
        passwordField.setName(passLabel.getText());
        char defaultEcho = passwordField.getEchoChar();

        AdaptableButton showPassBtn = new AdaptableButton(localShow, localHide);

        // instalando los componentes
        constraints.anchor = GridBagConstraints.LINE_END;
        add(userLabel, constraints);

        constraints.fill      = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(userField, constraints);

        row++;
        constraints.anchor    = GridBagConstraints.LINE_END;
        constraints.fill      = GridBagConstraints.NONE;
        constraints.gridwidth = 1;
        constraints.gridy = row;
        add(passLabel, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        add(passwordField, constraints);

        add(showPassBtn, constraints);

        if (isModule) {
            // local
            String localEdit = messages.getString("checkbox.enable_edition");

            // borde especial
            Border titledBorder = BorderFactory.createTitledBorder(getName());
            setBorder(BorderFactory.createCompoundBorder(titledBorder, emptyBorder));

            // contenido y comportamiento
            userField.setText(App.settings.getProperties().getProperty(Settings.KEY_USERNAME_DB));
            passwordField.setText(App.settings.getProperties().getProperty(Settings.KEY_PASSWORD_DB));
            userField.setEnabled(false);
            passwordField.setEnabled(false);
            showPassBtn.setEnabled(false);

            // instalación
            row++;
            JCheckBox editBtn = new JCheckBox(localEdit);
            constraints.anchor    = GridBagConstraints.LINE_START;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.gridy     = row;
            add(editBtn, constraints);

            // eventos
            editBtn.addActionListener(_ -> {
                userField.setEnabled(!userField.isEnabled());
                passwordField.setEnabled(!passwordField.isEnabled());
                showPassBtn.setEnabled(!showPassBtn.isEnabled());
            });
        } else {
            setBorder(emptyBorder);
        }

        // accesibilidad
        userLabel.setLabelFor(userField);
        passLabel.setLabelFor(passwordField);

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window source = SwingUtilities.windowForComponent(this);
        if ("apply".equals(e.getActionCommand()) || "apply&close".equals(e.getActionCommand())) {
            apply(source);
        }
    }

    private void apply(Window source) {
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
        App.settings.getProperties().setProperty(Settings.KEY_USERNAME_DB, user);
        App.settings.getProperties().setProperty(Settings.KEY_PASSWORD_DB, pass);
        success = DataBank.testConnection(source);
    }

    public boolean isConnectionSet() {
        return success;
    }

}
