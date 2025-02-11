package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.panel.ConnectionForm;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static java.awt.event.ActionEvent.ACTION_PERFORMED;

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
        String comments = messages.getString("ini.comments");

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
        applyButton.addActionListener(evt -> {
            connectionForm.actionPerformed(new ActionEvent(evt, ACTION_PERFORMED, "apply"));
            success = connectionForm.isConnectionSet();
            App.settings.applyChanges(SwingUtilities.windowForComponent(applyButton), comments);
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
