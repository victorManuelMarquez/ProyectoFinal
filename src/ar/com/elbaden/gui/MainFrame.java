package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(640, 480));
        add(new JLabel(title)); // demostrativo
    }

    public static void createAndShow(String title) {
        MainFrame frame = new MainFrame(title);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
