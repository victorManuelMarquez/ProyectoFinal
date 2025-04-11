package ar.com.elbaden.main;

import ar.com.elbaden.gui.LoadingScreenFrame;

import javax.swing.*;

public class App implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        LoadingScreenFrame.createAndShow();
    }

}
