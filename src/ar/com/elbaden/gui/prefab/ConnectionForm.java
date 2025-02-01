package ar.com.elbaden.gui.prefab;

import ar.com.elbaden.gui.button.AdaptableButton;
import ar.com.elbaden.gui.input.FieldMargin;
import ar.com.elbaden.gui.input.FilteredPasswordField;
import ar.com.elbaden.gui.input.FilteredTextField;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ConnectionForm extends JPanel {

    public ConnectionForm(Boolean titled) throws MissingResourceException {
        super(new GridBagLayout());

        ResourceBundle message;
        message = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localUser = message.getString("label.user_database");
        String localPass = message.getString("label.password_database");
        String localShow = message.getString("button.show");
        String localHide = message.getString("button.hide");

        setName(message.getString("literal.connection"));

        // variables
        final int space = 8, margin = 4;
        int row = 0;

        // componentes
        Border emptyBorder = BorderFactory.createEmptyBorder(space, space, space, space);

        if (titled) {
            Border titledBorder = BorderFactory.createTitledBorder(getName());
            setBorder(BorderFactory.createCompoundBorder(titledBorder, emptyBorder));
        } else {
            setBorder(emptyBorder);
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(margin, margin, margin, margin);
        constraints.weightx = 1.0;

        JLabel userLabel = new JLabel(localUser);

        JLabel passLabel = new JLabel(localPass);

        FilteredTextField userField = new FilteredTextField("^(?!\\d)\\w+$", 4, 16);
        userField.setMargin(new FieldMargin());
        userField.setName(userLabel.getText());

        FilteredPasswordField passwordField = new FilteredPasswordField("^\\w+$", 8, 16);
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

}
