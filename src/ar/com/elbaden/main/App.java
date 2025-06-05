package ar.com.elbaden.main;

import ar.com.elbaden.gui.AppLauncher;

import javax.swing.*;

public class App {

    public static final String FOLDER = ".baden";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                AppLauncher launcher = new AppLauncher();
                launcher.addPropertyChangeListener(evt -> {
                    if ("countdown".equals(evt.getPropertyName())) {
                        System.out.println(evt.getNewValue());
                    }
                });
                launcher.execute();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

}
