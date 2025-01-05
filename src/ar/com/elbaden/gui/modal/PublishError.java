package ar.com.elbaden.gui.modal;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;

public final class PublishError extends JDialog {

    private final Exception exception;

    public PublishError(Frame owner, Exception exception) {
        super(owner, exception.getClass().getSimpleName());
        this.exception = exception;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(true);
        getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        setUndecorated(getRootPane().getWindowDecorationStyle() != JRootPane.NONE);
        installComponents();
    }

    private void installComponents() {
        String localMsgTitle = "Mensaje";
        String localDetailsTitle = "Detalles";

        Border emptyBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);

        JPanel messagePanel = new JPanel(new BorderLayout());

        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        JLabel errorIconContainer = new JLabel(errorIcon);
        errorIconContainer.setBorder(emptyBorder);
        messagePanel.add(errorIconContainer, BorderLayout.WEST);

        JPanel messagesContainer = new JPanel(new GridLayout(0, 1));
        messagesContainer.setBorder(emptyBorder);
        messagePanel.add(messagesContainer);

        messagesContainer.add(new JLabel(getException().getMessage()));

        if (getException() instanceof SQLException sql) {
            String localState = "Código de error SQL:";
            String localVendor = "Código de error de Proveedor:";
            String sqlState = String.format(localState + " %s", sql.getSQLState());
            String sqlVendor = String.format(localVendor + " %s", sql.getErrorCode());
            messagesContainer.add(new JLabel(sqlState));
            messagesContainer.add(new JLabel(sqlVendor));
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(emptyBorder);
        tabs.addTab(localMsgTitle, messagePanel);
        add(tabs);

        if (getException().getCause() != null) {
            JTextArea detailsArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(360, 240));
            JPanel detailsPanel = new JPanel();
            detailsPanel.add(scrollPane);
            tabs.addTab(localDetailsTitle, detailsPanel);
            publishCauses(getException().getCause(), detailsArea);
        }

        String localClose = "Cerrar";
        JButton closeBtn = new JButton(localClose);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(_ -> dispose());
    }

    private void publishCauses(Throwable throwable, JTextArea textArea) {
        for (Throwable cause = throwable.getCause(); cause != null; cause = cause.getCause()) {
            textArea.append(cause.getLocalizedMessage());
        }
    }

    public static void createAndShow(JFrame owner, Exception exception) {
        PublishError publisher = new PublishError(owner, exception);
        publisher.pack();
        publisher.setLocationRelativeTo(owner);
        publisher.setVisible(true);
    }

    public Exception getException() {
        return exception;
    }

}
