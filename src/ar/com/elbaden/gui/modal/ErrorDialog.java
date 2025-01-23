package ar.com.elbaden.gui.modal;

import ar.com.elbaden.error.ResourceBundleException;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ErrorDialog extends MasterDialog {

    private ErrorDialog(Window owner, Exception exception) throws ResourceBundleException {
        super(owner, exception.getClass().getSimpleName());
        // nuevos ajustes
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        }
        ResourceBundle messages;
        try {
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new ResourceBundleException(e);
        }

        // contenido local
        String localBtnOk = messages.getString("button.ok");
        String messageTabTitle = messages.getString("error_dialog.message");
        String detailsTabTitle = messages.getString("error_dialog.details");

        // componentes
        Border emptyBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Dimension preferredSize = new Dimension(360, 240);
        Dimension maximumSize = new Dimension(640, 480);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(emptyBorder);

        JPanel messageTab = new JPanel(new BorderLayout(8, 0));
        messageTab.setBorder(emptyBorder);
        tabbedPane.addTab(messageTabTitle, messageTab);

        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        messageTab.add(new JLabel(errorIcon), BorderLayout.WEST);

        JPanel messagesPanel = new JPanel(null);
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

        JScrollPane messageScrollPane = new JScrollPane(messagesPanel);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messageScrollPane.setMaximumSize(maximumSize);
        messageTab.add(messageScrollPane);

        JLabel messageLabel = new JLabel();
        messagesPanel.add(messageLabel);

        JButton okButton = new JButton(localBtnOk);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        // si la excepción tiene errores encadenados
        if (exception.getCause() != null) {
            // componentes
            JPanel detailsTab = new JPanel(new BorderLayout());

            JTextArea details = new JTextArea();
            details.setEditable(false);

            JScrollPane detailsScrollPane = new JScrollPane(details);
            detailsScrollPane.setPreferredSize(preferredSize);

            // instalando los componentes
            detailsTab.add(detailsScrollPane);
            tabbedPane.addTab(detailsTabTitle, detailsTab);

            // publico los detalles
            for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
                details.append(cause.getLocalizedMessage() + System.lineSeparator());
            }
        }

        // instalando los componentes en el dialog
        getContentPane().add(tabbedPane);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // agrego el mensaje del error
        if (exception instanceof SQLException sqlError) {
            messageLabel.setText(sqlError.getLocalizedMessage());
            String localSQLState = messages.getString("error_dialog.sql_state");
            String localSQLVendor = messages.getString("error_dialog.sql_vendor");
            String sqlState = MessageFormat.format(localSQLState, sqlError.getSQLState());
            String sqlVendor = MessageFormat.format(localSQLVendor, Integer.toString(sqlError.getErrorCode()));
            // nuevos componentes para estos atributos
            messagesPanel.add(new JLabel(sqlState));
            messagesPanel.add(new JLabel(sqlVendor));
        } else {
            messageLabel.setText(exception.getLocalizedMessage());
        }

        // eventos
        okButton.addActionListener(_ -> dispose());

        SwingUtilities.invokeLater(okButton::requestFocusInWindow);
    }

    public static void createAndShow(Window root, Exception exception) {
        try {
            ErrorDialog dialog = new ErrorDialog(root, exception);
            dialog.pack();
            dialog.setLocationRelativeTo(root);
            dialog.setVisible(true);
        } catch (ResourceBundleException e) {
            throw new RuntimeException(e);
        }
    }

}
