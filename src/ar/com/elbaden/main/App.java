package ar.com.elbaden.main;

import ar.com.elbaden.gui.FontChooserDialog;

import javax.swing.*;

public class App implements Runnable {

    public static final String MESSAGES = "i18n/messages";

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(info.getClassName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        Object selection = FontChooserDialog.createAndShow(null);
        System.out.println("Fuente seleccionada: " + selection);
    }

}
