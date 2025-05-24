package ar.com.elbaden.main;

import ar.com.elbaden.gui.FontChooser;

import javax.swing.*;
import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getGlobal();
    public static final String MESSAGES = "i18n/messages";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println(FontChooser.createAndShow(null));
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }
        });
    }

}
