package ar.com.elbaden.gui.modal;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public final class PublishError extends MasterDialog {

    private final Exception exception;

    public PublishError(Frame owner, Exception exception) {
        super(owner, exception.getClass().getSimpleName());
        this.exception = exception;
        installComponents();
    }

    private void installComponents() {
        String localMsgTitle = "Mensaje";
        String localDetailsTitle = "Detalles";
        String localClose = "Cerrar";
        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        JTabbedPane tabs = new JTabbedPane();
        JButton closeBtn = new JButton(localClose);

        Border emptyBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);

        JPanel messagePanel = new JPanel(new BorderLayout());

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(_ -> dispose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                closeBtn.requestFocusInWindow();
            }
        });
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
