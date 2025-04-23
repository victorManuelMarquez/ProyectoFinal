package ar.com.elbaden.main;

import ar.com.elbaden.gui.FontsLoader;
import ar.com.elbaden.gui.LoadingFontsDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class App implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        LoadingFontsDialog dialog = new LoadingFontsDialog(null, "Cargando...");
        FontsLoader loader = new FontsLoader(dialog);
        loader.addPropertyChangeListener(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        loader.execute();
        dialog.setVisible(true);
        List<Font> fontList = dialog.getFontList();
        if (fontList != null) {
            fontList.forEach(System.out::println);
            System.out.println("Total: " + fontList.size());
        }
        System.out.println("Fin del programa.");
    }

}
