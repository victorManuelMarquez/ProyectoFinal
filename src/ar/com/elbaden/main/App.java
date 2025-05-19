package ar.com.elbaden.main;

import ar.com.elbaden.gui.FontChooser;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> System.out.println(FontChooser.createAndShow(null)));
    }

}
