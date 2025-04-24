package ar.com.elbaden.main;

import ar.com.elbaden.gui.FontChooserDialog;

import javax.swing.*;

public class App implements Runnable {

    public static void main(String[] args) {
        // aplico el tema Nimbus para probar el rendimiento y aspecto visual de mi programa.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        System.out.println("Fuente seleccionada: " + FontChooserDialog.createAndShow(null));
        System.out.println("Fin del programa.");
    }

}
