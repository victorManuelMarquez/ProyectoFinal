package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.text.Caret;
import java.awt.*;
import java.util.logging.Logger;

public class LauncherFrame extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(LauncherFrame.class.getName());

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private LauncherFrame(String title) throws HeadlessException {
        super(title);
        // componentes
        JTextArea infoArea = new JTextArea(15, 40);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        JProgressBar progressBar = new JProgressBar();

        // instalando componentes
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        /* ajustes */
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        }
        // área de texto
        infoArea.setEditable(false);
        infoArea.setFocusable(false);
        Caret caret = infoArea.getCaret();
        caret.setVisible(false);
        // barra de progreso
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
    }

    public static void createAndShow() {
        try {
            String title = App.messages.getString("launcher.title");
            LauncherFrame frame = new LauncherFrame(title);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            LOGGER.severe(e.getMessage());
        }
    }

}
