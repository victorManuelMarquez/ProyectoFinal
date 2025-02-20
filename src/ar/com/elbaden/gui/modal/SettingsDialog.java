package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.panel.ConnectionForm;
import ar.com.elbaden.gui.panel.GeneralSettings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class SettingsDialog extends MasterDialog {

    public SettingsDialog(Window owner, String title) throws MissingResourceException {
        super(owner, title);
        setResizable(false);

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localApply = messages.getString("button.apply");
        String localApplyClose = messages.getString("button.apply_close");
        String localCancel = messages.getString("button.cancel");
        String comments = messages.getString("ini.comments");

        // componentes
        JScrollPane scrollMainContent = new JScrollPane();
        Border emptyBorder = BorderFactory.createEmptyBorder(4, 4, 0, 4);
        scrollMainContent.setBorder(emptyBorder);

        JPanel mainContent = new JPanel(null);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        scrollMainContent.setViewportView(mainContent);

        GeneralSettings generalSettings = new GeneralSettings();
        mainContent.add(generalSettings);

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
        btnApply.addActionListener(generalSettings);
        btnApply.addActionListener(connectionForm);
        btnApply.addActionListener(_ -> {
            Window root = SwingUtilities.windowForComponent(btnApply);
            App.settings.applyChanges(root, comments);
        });

        btnApplyClose.addActionListener(generalSettings);
        btnApplyClose.addActionListener(connectionForm);
        btnApplyClose.addActionListener(_ -> {
            App.settings.applyChanges(SwingUtilities.windowForComponent(btnApplyClose), comments);
            dispose();
        });

        btnCancel.addActionListener(_ -> dispose());
    }

    public void recalculateDimensions() {
        int actualWidth = getWidth() + UIManager.getInt("ScrollBar.width");
        int actualHeight = getHeight();
        int preferredWidth = Math.min(actualWidth, 640);
        int preferredHeight = Math.min(actualHeight, 360);
        setSize(new Dimension(preferredWidth, preferredHeight));
    }

    public static void createAndShow(Window window, String title) {
        SettingsDialog dialog = new SettingsDialog(window, title);
        dialog.pack();
        dialog.recalculateDimensions();
        dialog.setLocationRelativeTo(window);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

}
