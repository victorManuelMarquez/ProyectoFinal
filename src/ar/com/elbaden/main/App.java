package ar.com.elbaden.main;

import ar.com.elbaden.gui.LauncherFrame;

import javax.swing.*;
import java.util.ResourceBundle;

public class App {

    public static ResourceBundle messages;

    static {
        // cargo el archivo de localización
        messages = ResourceBundle.getBundle("i18n/messages");
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(LauncherFrame::createAndShow);
    }

}
