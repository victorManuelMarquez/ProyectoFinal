package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.area.MessageArea;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public final class AboutDialog extends MasterDialog {

    private AboutDialog(Window owner, String title) {
        super(owner, title);
        setLayout(new GridBagLayout());
        setResizable(false);

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localClose = messages.getString("button.close");
        String localAbout = messages.getString("app.about");
        String localInfo = messages.getString("java_logo.info");

        // componentes
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;

        ImageIcon javaIcon = null;
        URL urlJavaIcon = getClass().getResource("/images/java_logo.png");
        if (urlJavaIcon != null) {
            javaIcon = new ImageIcon(urlJavaIcon);
        }

        MessageArea messageArea = new MessageArea();
        messageArea.setText(localAbout);

        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton closeButton = new JButton(localClose);

        // instalando los componentes en el dialog
        int col = 0;

        if (javaIcon != null) {
            String url = "https://www.flaticon.com/free-icons/java";

            JButton urlIconWebSite = new JButton(javaIcon);
            urlIconWebSite.setBorderPainted(false);
            urlIconWebSite.setContentAreaFilled(false);
            urlIconWebSite.setFocusPainted(false);
            urlIconWebSite.setFocusable(false);
            urlIconWebSite.setToolTipText("<HTML>" + localInfo + "<br>" + url + "</HTML>");

            constraints.gridheight = 2;
            constraints.gridx = col;
            constraints.insets = new Insets(16, 8, 16, 8);
            getContentPane().add(urlIconWebSite, constraints);
            col += 1;

            urlIconWebSite.addActionListener(_ -> {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        URI webSite = new URI(url);
                        desktop.browse(webSite);
                    } catch (URISyntaxException | IOException exception) {
                        ErrorDialog.createAndShow(getOwner(), exception);
                    } catch (UnsupportedOperationException e) {
                        MessageDialog.createAndShow(getOwner(), localInfo, url, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridheight = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = col;
        constraints.weighty = 1.0;
        getContentPane().add(messageScrollPane, constraints);

        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = col;
        constraints.insets = new Insets(8, 16, 8, 16);
        constraints.weighty = .0;
        getContentPane().add(closeButton, constraints);

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
