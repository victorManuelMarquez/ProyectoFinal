package ar.com.elbaden.gui.modal;

import ar.com.elbaden.gui.area.MessageArea;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ErrorDialog extends MasterDialog {

    private ErrorDialog(Window owner, Exception exception) throws MissingResourceException {
        super(owner, exception.getClass().getSimpleName());
        // nuevos ajustes
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        }
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localBtnOk = messages.getString("button.ok");
        String messageTabTitle = messages.getString("error_dialog.message");
        String detailsTabTitle = messages.getString("error_dialog.details");

        // componentes
        Border emptyBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(emptyBorder);

        JPanel messageTab = new JPanel(new BorderLayout(8, 0));
        messageTab.setBorder(emptyBorder);
        tabbedPane.addTab(messageTabTitle, messageTab);

        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        messageTab.add(new JLabel(errorIcon), BorderLayout.WEST);

        JPanel messagesPanel = new JPanel(null);
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messageTab.add(messagesPanel);

        MessageArea messageArea = new MessageArea();
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(null);
        messagesPanel.add(messageScrollPane);

        JButton okButton = new JButton(localBtnOk);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        // si la excepción tiene errores encadenados
        if (exception.getCause() != null) {
            // componentes
            JPanel detailsTab = new JPanel(new BorderLayout());

            JTextArea details = new JTextArea();
            details.setMargin(UIManager.getInsets("TextPane.margin"));
            details.setEditable(false);
            details.setLineWrap(true);
            details.setWrapStyleWord(true);

            JScrollPane detailsScrollPane = new JScrollPane(details);

            // instalando los componentes
            detailsTab.add(detailsScrollPane);
            tabbedPane.addTab(detailsTabTitle, detailsTab);

            // publico los detalles
            for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
                details.append(cause.getLocalizedMessage() + System.lineSeparator());
            }
        }

        // agrego el mensaje del error
        String message = exception.getMessage();

        if (exception instanceof SQLException sqlError) {
            messageArea.setText(message + System.lineSeparator());
            String localSQLState = messages.getString("error_dialog.sql_state");
            String localSQLVendor = messages.getString("error_dialog.sql_vendor");
            String sqlState = MessageFormat.format(localSQLState, sqlError.getSQLState());
            String sqlVendor = MessageFormat.format(localSQLVendor, Integer.toString(sqlError.getErrorCode()));
            // agrego los detalles
            messageArea.append(sqlState + System.lineSeparator());
            messageArea.append(sqlVendor);
        } else {
            messageArea.setText(message);
        }

        // instalando los componentes en el dialog
        getContentPane().add(tabbedPane);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // eventos
        okButton.addActionListener(_ -> dispose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                okButton.requestFocusInWindow();
            }
        });
    }

    public static void createAndShow(Window root, Exception exception) {
        ErrorDialog dialog = new ErrorDialog(root, exception);
        dialog.pack();
        dialog.setLocationRelativeTo(root);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

}
