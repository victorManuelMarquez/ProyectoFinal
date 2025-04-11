package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class LoadingScreenFrame extends JFrame {

    private LoadingScreenFrame() throws HeadlessException {
        // ajustes
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // si el tema actual soporta personalización de ventanas
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        getContentPane().add(scrollPane);
    }

    public static void createAndShow() {
        LoadingScreenFrame frame = new LoadingScreenFrame();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
