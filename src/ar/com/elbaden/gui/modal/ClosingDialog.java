package ar.com.elbaden.gui.modal;

import ar.com.elbaden.error.ResourceBundleException;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ClosingDialog extends MasterDialog {

    private int legacyOption = JOptionPane.CANCEL_OPTION;

    private ClosingDialog(Window owner) throws ResourceBundleException {
        super(owner, null);
        ResourceBundle messages;
        try {
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new ResourceBundleException(e);
        }

        // contenido local
        String localTitle = messages.getString("closing_dialog.title");
        String localMessage = messages.getString("closing_dialog.message");
        String localDoNotAsk = messages.getString("checkbox.do_not_ask_again");
        String localExit = messages.getString("button.exit");
        String localCancel = messages.getString("button.cancel");

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
        JCheckBox doNotAskAgain = new JCheckBox(localDoNotAsk);
        doNotAskAgain.setEnabled(false);

        JPanel contentPanel = new JPanel(null);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        emptyBorder = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
        contentPanel.setBorder(emptyBorder);
        contentPanel.add(messageLabel);
        contentPanel.add(doNotAskAgain);

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
        exitButton.addActionListener(_ -> {
            legacyOption = JOptionPane.OK_OPTION;
            dispose();
        });

        cancelButton.addActionListener(_ -> dispose());
    }

    public static int createAndShow(Window root) {
        try {
            ClosingDialog dialog = new ClosingDialog(root);
            dialog.pack();
            dialog.setLocationRelativeTo(root);
            dialog.setMinimumSize(dialog.getSize());
            dialog.setResizable(false);
            dialog.setVisible(true);
            return dialog.legacyOption;
        } catch (ResourceBundleException e) {
            return -1;
        }
    }

}
