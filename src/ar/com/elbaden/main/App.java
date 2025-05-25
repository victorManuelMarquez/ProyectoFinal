package ar.com.elbaden.main;

import ar.com.elbaden.gui.LoadingScreen;

import javax.swing.*;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getGlobal();

    public static final String MESSAGES = "i18n/messages";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ResourceBundle messages = ResourceBundle.getBundle(MESSAGES);
                Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> LOGGER.info(messages.getString("log.info.exitProgram")), "exitLog")
                );
                LoadingScreen.createAndShow(messages);
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }
        });
    }

}
