package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.area.MessageArea;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public final class AboutDialog extends MasterDialog {

    private AboutDialog(Window owner, String title) {
        super(owner, title);
        setResizable(false);

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localClose = messages.getString("button.close");

        // componentes
        MessageArea messageArea = new MessageArea();
        messageArea.setText("""
                El badén v1.0 - Sistema de gestión de stock
                Este sistema está desarrollado como proyecto final de la materia programación IV
                desarrollado originalmente en 2019.
                """);

        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        messageScrollPane.getViewport().setPreferredSize(messageArea.getMinimumSize());

        JButton closeButton = new JButton(localClose);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        // instalando los componentes en el dialog
        getContentPane().add(messageScrollPane);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // eventos
        closeButton.addActionListener(_ -> dispose());
    }

    public static void createAndShow(Window root, String title) {
        AboutDialog dialog = new AboutDialog(root, title);
        dialog.pack();
        dialog.setLocationRelativeTo(root);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

}
