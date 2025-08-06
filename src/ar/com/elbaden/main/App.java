package ar.com.elbaden.main;

import ar.com.elbaden.gui.LauncherFrame;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(LauncherFrame::createAndShow);
    }

}
