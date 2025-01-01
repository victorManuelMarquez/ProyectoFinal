package ar.com.elbaden.main;

import javax.swing.*;

public class App implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {}

}
