package ar.com.elbaden.gui.modal;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.ConnectionPrefab;
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
        String localOk = "Aceptar";
        String localCancel = "Cancelar";

        JPanel buttonsPanel = new JPanel();
        add(buttonsPanel, BorderLayout.SOUTH);

        FlowLayout flowLayout = (FlowLayout) buttonsPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);

        JButton okBtn = new JButton(localOk);
        buttonsPanel.add(okBtn);

        JButton cancelBtn = new JButton(localCancel);
        buttonsPanel.add(cancelBtn);

        ConnectionPrefab prefab = new ConnectionPrefab();
        getContentPane().add(prefab);

        int minUserLength = 4;
        int minPassLength = 8;
        int maxUserLength = 16;
        int maxPassLength = 16;

        JTextField userField = prefab.getUserField();
        JPasswordField passwordField = prefab.getPassField();

        Strings.installDocumentFilterValidator(userField, "^(?!\\d)\\w+$", minUserLength, maxUserLength);
        Strings.installDocumentFilterValidator(passwordField, "^\\w+$", minPassLength, maxPassLength);

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
