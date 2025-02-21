package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.area.MessageArea;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public final class AboutDialog extends MasterDialog {

    private AboutDialog(Window owner, String title) {
        super(owner, title);
        setResizable(false);

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localClose = messages.getString("button.close");
        String localAbout = messages.getString("app.about");

        // componentes
        ImageIcon javaIcon = null;
        URL urlJavaIcon = getClass().getResource("/images/java_logo.png");
        if (urlJavaIcon != null) {
            javaIcon = new ImageIcon(urlJavaIcon);
        }

        MessageArea messageArea = new MessageArea();
        messageArea.setText(localAbout);

        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        Border emptyBorder = BorderFactory.createEmptyBorder(16, 8, 16, 8);
        messageScrollPane.setBorder(emptyBorder);
        messageScrollPane.getViewport().setPreferredSize(messageArea.getMinimumSize());

        JButton closeButton = new JButton(localClose);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        // instalando los componentes en el dialog
        if (javaIcon != null) {
            JLabel javaLabel = new JLabel(javaIcon);
            javaLabel.setBorder(emptyBorder);
            getContentPane().add(javaLabel, BorderLayout.WEST);
        }
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
