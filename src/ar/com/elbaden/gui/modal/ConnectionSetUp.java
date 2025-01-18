package ar.com.elbaden.gui.modal;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.main.App;
import ar.com.elbaden.utils.Strings;

import javax.swing.*;
import java.awt.*;

public final class ConnectionSetUp extends MasterDialog {

    private boolean connectionSuccess = false;

    private ConnectionSetUp(Frame owner, String title) {
        super(owner, title);
        setResizable(false);
        installComponents();
    }

    private void installComponents() {
        String localUserTxt = "Usuario:";
        String localPassTxt = "Contraseña:";
        String localShowTxt = "Mostrar";
        String localHideTxt = "Ocultar";
        String localOk = "Aceptar";
        String localCancel = "Cancelar";

        JLabel labelUser = new JLabel(localUserTxt);
        JLabel labelPass = new JLabel(localPassTxt);

        Insets fieldMargin = UIManager.getInsets("TextPane.margin");

        int minUserLength = 4;
        int maxUserLength = 16;
        JTextField userField = new JTextField();
        userField.setName(localUserTxt.replace(':', Character.MIN_VALUE));
        userField.setMargin(fieldMargin);
        labelUser.setLabelFor(userField);

        Strings.installDocumentFilterValidator(userField, "^(?!\\d)\\w+$", minUserLength, maxUserLength);

        int minPassLength = 8;
        int maxPassLength = 32;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setName(localPassTxt.replace(':', Character.MIN_VALUE));
        passwordField.setColumns(12);
        passwordField.setMargin(fieldMargin);
        labelPass.setLabelFor(passwordField);

        Strings.installDocumentFilterValidator(passwordField, "^\\w+$", minPassLength, maxPassLength);

        JButton showButton = new JButton(localShowTxt);
        Strings.fitDynamicContent(showButton, localShowTxt, localHideTxt);

        JPanel inputsPanel = new JPanel(new GridBagLayout());
        add(inputsPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.anchor = GridBagConstraints.LINE_END;
        inputsPanel.add(labelUser, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        inputsPanel.add(userField, gbc);

        gbc.gridy = 1;

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        inputsPanel.add(labelPass, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        inputsPanel.add(passwordField, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = .0;
        inputsPanel.add(showButton, gbc);

        JPanel buttonsPanel = new JPanel();
        add(buttonsPanel, BorderLayout.SOUTH);

        FlowLayout flowLayout = (FlowLayout) buttonsPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);

        JButton okBtn = new JButton(localOk);
        buttonsPanel.add(okBtn);

        JButton cancelBtn = new JButton(localCancel);
        buttonsPanel.add(cancelBtn);

        showButton.addActionListener(_ -> {
            char newChar = Character.MIN_VALUE;
            char defaultChar = (Character) UIManager.get("PasswordField.echoChar");
            passwordField.setEchoChar(passwordField.getEchoChar() == defaultChar ? newChar : defaultChar);
            showButton.setText(passwordField.getEchoChar() == newChar ? localHideTxt : localShowTxt);
        });
        okBtn.addActionListener(_ -> {
            String userValue = userField.getText();
            String passValue = new String(passwordField.getPassword());
            if (userValue.length() < minUserLength) {
                String localMessage = "El nombre de usuario es muy corto.";
                String localTitle = "Atención";
                int icon = JOptionPane.ERROR_MESSAGE;
                PublishMessage.createAndShow(getOwner(), localMessage, localTitle, icon);
                return;
            }
            if (passValue.length() < minPassLength) {
                String localMessage = "La contraseña es muy corta.";
                String localTitle = "Atención";
                int icon = JOptionPane.ERROR_MESSAGE;
                PublishMessage.createAndShow(getOwner(), localMessage, localTitle, icon);
                return;
            }
            App.properties.setProperty(Settings.KEY_USER_DATABASE, userValue);
            App.properties.setProperty(Settings.KEY_PASSWORD_DATABASE, passValue);
            connectionSuccess = DataBank.canConnect((JFrame) getOwner());
            if (connectionSuccess) {
                Settings.storeExternal((JFrame) getOwner());
            }
            dispose();
        });
        cancelBtn.addActionListener(_ -> dispose());
    }

    public static boolean createAndShow(JFrame owner) {
        String localTitle = "Configure la conexión";
        ConnectionSetUp setUp = new ConnectionSetUp(owner, localTitle);
        setUp.pack();
        setUp.setLocationRelativeTo(owner);
        setUp.setVisible(true);
        return setUp.connectionSuccess;
    }

}
