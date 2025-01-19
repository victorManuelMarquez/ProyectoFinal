package ar.com.elbaden.gui;

import ar.com.elbaden.utils.Strings;

import javax.swing.*;
import java.awt.*;

public final class ConnectionPrefab extends JPanel {

    private final GridBagConstraints constraints;
    private final JTextField userField;
    private final JPasswordField passField;
    private final JButton showPassBtn;

    public ConnectionPrefab() {
        super(new GridBagLayout());

        String localUserTxt = "Usuario:";
        String localPassTxt = "Contraseña:";
        String localShowTxt = "Mostrar";
        String localHideTxt = "Ocultar";

        this.constraints = new GridBagConstraints();
        getConstraints().weightx = 1.0;
        getConstraints().insets = new Insets(4, 4, 4, 4);

        JLabel userLabel = new JLabel(localUserTxt);
        JLabel passLabel = new JLabel(localPassTxt);

        Insets fieldMargin = UIManager.getInsets("TextPane.margin");

        DocumentValidator userValidator = new DocumentValidator("^(?!\\d)\\w+$", 4, 16);
        DocumentValidator passValidator = new DocumentValidator("^\\w+$", 32);

        this.userField = new JTextField();
        this.passField = new JPasswordField();
        this.showPassBtn = new JButton(localShowTxt);

        userField.setName(localUserTxt.replace(':', Character.MIN_VALUE));
        passField.setName(localPassTxt.replace(':', Character.MIN_VALUE));

        installValidation(userField, userValidator);
        installValidation(passField, passValidator);

        Strings.fitDynamicContent(showPassBtn, localShowTxt, localHideTxt);

        userLabel.setLabelFor(userField);
        getConstraints().anchor = GridBagConstraints.LINE_END;
        add(userLabel, getConstraints());

        userField.setMargin(fieldMargin);
        getConstraints().anchor = GridBagConstraints.CENTER;
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        getConstraints().gridwidth = GridBagConstraints.REMAINDER;
        add(userField, getConstraints());

        passLabel.setLabelFor(passField);
        getConstraints().anchor = GridBagConstraints.LINE_END;
        getConstraints().fill = GridBagConstraints.NONE;
        getConstraints().gridwidth = 1;
        add(passLabel, getConstraints());

        passField.setMargin(fieldMargin);
        passField.setColumns(12);
        getConstraints().anchor = GridBagConstraints.CENTER;
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        getConstraints().gridwidth = GridBagConstraints.RELATIVE;
        add(passField, getConstraints());

        getConstraints().fill = GridBagConstraints.NONE;
        getConstraints().weightx = 0.0;
        add(showPassBtn, getConstraints());

        char emptyChar = Character.MIN_VALUE;
        char defaultChar = passField.getEchoChar();

        showPassBtn.addActionListener(_ -> {
            if (getPassField().getEchoChar() == defaultChar) {
                getPassField().setEchoChar(emptyChar);
                getShowPassBtn().setText(localHideTxt);
            } else {
                getPassField().setEchoChar(defaultChar);
                getShowPassBtn().setText(localShowTxt);
            }
        });
    }

    private void installValidation(JTextField field, DocumentValidator validator) {
        field.setDocument(validator);
        field.getCaret().addChangeListener(validator.getChangeListener());
        field.setToolTipText(validator.getSuggestedTooltip());
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }

    public JTextField getUserField() {
        return userField;
    }

    public JPasswordField getPassField() {
        return passField;
    }

    public JButton getShowPassBtn() {
        return showPassBtn;
    }

}
