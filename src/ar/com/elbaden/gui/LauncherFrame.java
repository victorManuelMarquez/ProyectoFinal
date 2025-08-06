package ar.com.elbaden.gui;

import javax.swing.*;
import javax.swing.text.Caret;
import java.awt.*;

public class LauncherFrame extends JFrame {

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
            LauncherFrame frame = new LauncherFrame(LauncherFrame.class.getSimpleName());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

}
