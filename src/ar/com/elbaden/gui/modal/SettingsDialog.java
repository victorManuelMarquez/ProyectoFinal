package ar.com.elbaden.gui.modal;

import ar.com.elbaden.error.ResourceBundleException;
import ar.com.elbaden.gui.prefab.ConnectionForm;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SettingsDialog extends MasterDialog {

    public SettingsDialog(Window owner, String title) throws ResourceBundleException {
        super(owner, title);
        setResizable(false);

        ResourceBundle messages;
        try {
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new ResourceBundleException(e);
        }

        // contenido local
        String localApply = messages.getString("button.apply");
        String localApplyClose = messages.getString("button.apply_close");
        String localCancel = messages.getString("button.cancel");

        // componentes
        JScrollPane scrollMainContent = new JScrollPane();

        JPanel mainContent = new JPanel(null);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        scrollMainContent.setViewportView(mainContent);

        ConnectionForm connectionForm = new ConnectionForm(true);
        mainContent.add(connectionForm);

        JButton btnApply = new JButton(localApply);
        btnApply.setActionCommand("apply");

        JButton btnApplyClose = new JButton(localApplyClose);
        btnApplyClose.setActionCommand("apply&close");

        JButton btnCancel = new JButton(localCancel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.add(btnApply);
        buttonsPanel.add(btnApplyClose);
        buttonsPanel.add(btnCancel);

        // instalando los componentes en el dialog
        getContentPane().add(scrollMainContent);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // eventos
        btnApply.addActionListener(connectionForm);

        btnApplyClose.addActionListener(connectionForm);

        btnCancel.addActionListener(_ -> dispose());
    }

    public void recalculateDimensions() {
        int actualWidth = getWidth() + UIManager.getInt("ScrollBar.width");
        int actualHeight = getHeight();
        int maxWidth = preferredMaxDimensions.width;
        int maxHeight = preferredMaxDimensions.height;
        int preferredWidth = Math.min(actualWidth, maxWidth);
        int preferredHeight = Math.min(actualHeight, maxHeight);
        setSize(new Dimension(preferredWidth, preferredHeight));
    }

    public static void createAndShow(Window window, String title) {
        try {
            SettingsDialog dialog = new SettingsDialog(window, title);
            dialog.pack();
            dialog.recalculateDimensions();
            dialog.setLocationRelativeTo(window);
            dialog.setMinimumSize(dialog.getSize());
            dialog.setVisible(true);
        } catch (ResourceBundleException e) {
            throw new RuntimeException(e);
        }
    }

}
