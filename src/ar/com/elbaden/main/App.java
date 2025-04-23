package ar.com.elbaden.main;

import ar.com.elbaden.gui.FontChooserDialog;

import javax.swing.*;

public class App implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        System.out.println("Fuente seleccionada: " + FontChooserDialog.createAndShow(null));
        System.out.println("Fin del programa.");
    }

}
