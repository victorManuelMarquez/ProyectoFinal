package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.area.MessageArea;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class MessageDialog extends MasterMessageDialog {

    private MessageDialog(Window owner, String title, String message, int legacyIcon) throws MissingResourceException {
        super(owner, title, legacyIcon);
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localOk = messages.getString("button.ok");

        // componentes
        Border emptyBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);

        JLabel iconLabel = new JLabel(getMessageIcon());
        iconLabel.setBorder(emptyBorder);

        MessageArea messageArea = new MessageArea();
        messageArea.setText(message);

        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(emptyBorder);

        JButton okButton = new JButton(localOk);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        // instalando componentes en el dialog
        getContentPane().add(iconLabel, BorderLayout.WEST);
        getContentPane().add(messageScrollPane);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // eventos
        okButton.addActionListener(_ -> dispose());
    }

    public static void createAndShow(Window root, String title, String message, int legacyIcon) {
        MessageDialog dialog = new MessageDialog(root, title, message, legacyIcon);
        dialog.pack();
        dialog.setLocationRelativeTo(root);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

}
