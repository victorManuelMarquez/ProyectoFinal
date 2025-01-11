package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
    }

    public static void createAndShow() {
        String localTitle = "Bienvenido";
        MainFrame frame = new MainFrame(localTitle);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
