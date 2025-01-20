package ar.com.elbaden.gui.modal;

import ar.com.elbaden.connection.DataBank;
import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.ConnectionPrefab;
import ar.com.elbaden.gui.DocumentValidator;
import ar.com.elbaden.main.App;

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

        JTextField userField = prefab.getUserField();
        JPasswordField passwordField = prefab.getPassField();

        okBtn.addActionListener(_ -> {
            if (DocumentValidator.verificationNeeded(userField)) return;
            if (DocumentValidator.verificationNeeded(passwordField)) return;
            String userValue = userField.getText();
            String passValue = new String(passwordField.getPassword());
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
