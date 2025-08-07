package ar.com.baden.main;

import ar.com.baden.gui.LauncherFrame;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LauncherFrame::createAndShow);
    }

}
