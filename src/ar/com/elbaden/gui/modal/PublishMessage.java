package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;

public final class PublishMessage extends MasterDialog {

    private final String message;

    private PublishMessage(Frame owner, String message, String title, int messageIcon) {
        super(owner, title);
        this.message = message;
        installComponents(messageIcon);
    }

    private void installComponents(int messageIcon) {
        String localClose = "Cerrar";

        JPanel mainContainer = new JPanel(new BorderLayout(8, 8));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        add(mainContainer);

        String propertyValue = "";
        switch (messageIcon) {
            case JOptionPane.INFORMATION_MESSAGE -> propertyValue = "OptionPane.informationIcon";
            case JOptionPane.QUESTION_MESSAGE -> propertyValue = "OptionPane.questionIcon";
            case JOptionPane.WARNING_MESSAGE -> {
                propertyValue = "OptionPane.warningIcon";
                getRootPane().setWindowDecorationStyle(JRootPane.WARNING_DIALOG);
            }
            case JOptionPane.ERROR_MESSAGE -> {
                propertyValue = "OptionPane.errorIcon";
                getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
            }
        }
        Icon icon = UIManager.getIcon(propertyValue);

        if (icon != null) {
            mainContainer.add(new JLabel(icon), BorderLayout.WEST);
        }

        mainContainer.add(new JLabel(getMessage()));

        JButton closeButton = new JButton(localClose);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(_ -> dispose());
    }

    public static void createAndShow(Component component, String message, String title, int messageIcon) {
        Window window = SwingUtilities.windowForComponent(component);
        JFrame root = null;
        if (window instanceof JFrame)
            root = (JFrame) window;
        else if (window instanceof JDialog dialog)
            root = (JFrame) dialog.getOwner();
        PublishMessage publisher = new PublishMessage(root, message, title, messageIcon);
        publisher.pack();
        publisher.setLocationRelativeTo(window);
        publisher.setVisible(true);
    }

    public String getMessage() {
        return message;
    }

}
