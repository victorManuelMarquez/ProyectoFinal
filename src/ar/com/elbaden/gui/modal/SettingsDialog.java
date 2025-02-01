package ar.com.elbaden.gui.modal;

import ar.com.elbaden.error.ResourceBundleException;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SettingsDialog extends MasterDialog {

    public SettingsDialog(Window owner, String title) throws ResourceBundleException {
        super(owner, title);

        ResourceBundle message;
        try {
            message = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new ResourceBundleException(e);
        }

        // contenido local
        String localApply = message.getString("button.apply");
        String localApplyClose = message.getString("button.apply_close");
        String localCancel = message.getString("button.cancel");

        // componentes

        // ***********************
        // agregar componentes faltantes....
        // ***********************

        JButton btnApply = new JButton(localApply);

        JButton btnApplyClose = new JButton(localApplyClose);

        JButton btnCancel = new JButton(localCancel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.add(btnApply);
        buttonsPanel.add(btnApplyClose);
        buttonsPanel.add(btnCancel);

        // instalando los componentes en el dialog
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // eventos
        btnCancel.addActionListener(_ -> dispose());
    }

    public static void createAndShow(Window window, String title) {
        try {
            SettingsDialog dialog = new SettingsDialog(window, title);
            dialog.pack();
            dialog.setLocationRelativeTo(window);
            dialog.setMinimumSize(dialog.getSize());
            dialog.setVisible(true);
        } catch (ResourceBundleException e) {
            throw new RuntimeException(e);
        }
    }

}
