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

        JTextArea simpleMessageArea = createMessageArea(exception.getMessage());
        Color alpha = new Color(0, 0, 0, 1);
        simpleMessageArea.setBackground(alpha);
        JScrollPane simpleMessageScroll = new JScrollPane();
        simpleMessageScroll.setBorder(BorderFactory.createEmptyBorder());

        JTabbedPane detailsTab = new JTabbedPane();
        JTextArea messageArea = createMessageArea(causes.isEmpty() ? "" : causes.getLast());
        messageArea.setBackground(alpha);
        JScrollPane messageScrollPane = new JScrollPane();
        JTextArea detailsArea = createMessageArea(exception.getMessage());
        JScrollPane detailsScrollPane = new JScrollPane();

        JPanel panel = new JPanel();
        JButton okButton = new JButton(App.messages.getString("ok"));

        // instalando componentes
        getRootPane().setDefaultButton(okButton);
        simpleMessageScroll.setViewportView(simpleMessageArea);
        cardsPanel.add(simpleMessageScroll, "simple");
        messageScrollPane.setViewportView(messageArea);
        detailsScrollPane.setViewportView(detailsArea);
        detailsTab.addTab(App.messages.getString("error"), messageScrollPane);
        detailsTab.addTab(App.messages.getString("details"), detailsScrollPane);
        cardsPanel.add(detailsTab, "detailed");
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

    private JTextArea createMessageArea(String message) {
        JTextArea textArea = new JTextArea(message);
        textArea.setColumns(40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    public static void createAndShow(Window owner, Exception exception) {
        try {
            String title = exception.getClass().getSimpleName();
            ErrorDialog dialog = new ErrorDialog(owner, title, exception);
            App.settings.updateFonts(dialog);
            dialog.pack();
            dialog.setLocationRelativeTo(owner);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
