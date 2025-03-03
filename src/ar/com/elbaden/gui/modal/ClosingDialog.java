package ar.com.elbaden.gui.modal;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ClosingDialog extends MasterDialog {

    private int legacyOption = JOptionPane.CANCEL_OPTION;

    private ClosingDialog(Window owner) throws MissingResourceException {
        super(owner, null);
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localTitle = messages.getString("closing_dialog.title");
        String localMessage = messages.getString("closing_dialog.message");
        String localDoNotAsk = messages.getString("checkbox.do_not_ask_again");
        String localExit = messages.getString("button.exit");
        String localCancel = messages.getString("button.cancel");
        String localComments = messages.getString("ini.comments");

        setTitle(localTitle);

        // componentes
        int margin = 8;

        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");

        JLabel questionLabel = new JLabel(questionIcon);
        questionLabel.setVerticalAlignment(SwingConstants.TOP);
        Border emptyBorder = BorderFactory.createEmptyBorder(margin * 2, margin * 2, margin, margin);
        questionLabel.setBorder(emptyBorder);

        JLabel messageLabel = new JLabel(localMessage);
        emptyBorder = BorderFactory.createEmptyBorder(margin, 0, margin, 0);
        messageLabel.setBorder(emptyBorder);

        // pendiente de funcionalidad
        JCheckBox disableConfirmation = new JCheckBox(localDoNotAsk);

        JPanel contentPanel = new JPanel(null);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        emptyBorder = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
        contentPanel.setBorder(emptyBorder);
        contentPanel.add(messageLabel);
        contentPanel.add(disableConfirmation);

        JButton exitButton = new JButton(localExit);

        JButton cancelButton = new JButton(localCancel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, margin, margin));
        buttonsPanel.add(exitButton);
        buttonsPanel.add(cancelButton);

        // instalando los componentes en el dialog
        getContentPane().add(questionLabel, BorderLayout.WEST);
        getContentPane().add(contentPanel);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        // eventos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                exitButton.requestFocusInWindow();
            }
        });

        disableConfirmation.addActionListener(_ -> {
            String value = Boolean.toString(!disableConfirmation.isSelected());
            App.settings.getProperties().setProperty(Settings.KEY_ASK_FOR_CLOSING, value);
            App.settings.applyChanges(this, localComments);
        });

        exitButton.addActionListener(_ -> {
            legacyOption = JOptionPane.OK_OPTION;
            dispose();
        });

        cancelButton.addActionListener(_ -> dispose());
    }

    public static int createAndShow(Window root) {
        ClosingDialog dialog = new ClosingDialog(root);
        dialog.pack();
        dialog.setLocationRelativeTo(root);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setResizable(false);
        dialog.setVisible(true);
        return dialog.legacyOption;
    }

}
