package ar.com.elbaden.gui.window;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ErrorDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(ErrorDialog.class.getName());

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private ErrorDialog(Window owner, String title, Exception exception) {
        super(owner, title);
        // variables
        Dimension maximum = new Dimension(640, 360);
        List<String> causes = new ArrayList<>();
        for (Throwable throwable = exception.getCause(); throwable != null; throwable = throwable.getCause()) {
            if (throwable.getMessage() != null) {
                causes.add(throwable.getMessage());
            }
        }

        // componentes
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel cardsPanel = new JPanel(null);
        CardLayout cardLayout = new CardLayout();
        cardsPanel.setLayout(cardLayout);

        JLabel simpleMessageLabel = new JLabel(exception.getMessage());
        simpleMessageLabel.setMaximumSize(maximum);

        JPanel panel = new JPanel();
        JButton okButton = new JButton(App.messages.getString("ok"));

        // instalando componentes
        getRootPane().setDefaultButton(okButton);
        cardsPanel.add(simpleMessageLabel);
        cardLayout.show(cardsPanel, causes.isEmpty() ? "simple" : "detailed"); // detalles no implementados todavía
        mainPanel.add(cardsPanel);
        panel.add(okButton);
        mainPanel.add(panel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);

        // ajustes
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        }

        // eventos
        SwingUtilities.invokeLater(okButton::requestFocusInWindow);
        okButton.addActionListener(_ -> dispose());
    }

    public static void createAndShow(Window owner, Exception exception) {
        try {
            String title = exception.getClass().getSimpleName();
            ErrorDialog dialog = new ErrorDialog(owner, title, exception);
            dialog.pack();
            dialog.setLocationRelativeTo(owner);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
