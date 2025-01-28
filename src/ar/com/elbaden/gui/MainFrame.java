package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(MAXIMIZED_BOTH)) {
            setExtendedState(MAXIMIZED_BOTH);
        }
    }

    public static void createAndShow() {
        MainFrame frame = new MainFrame("Demo");
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
