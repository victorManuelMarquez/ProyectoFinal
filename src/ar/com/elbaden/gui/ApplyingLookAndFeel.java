package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ApplyingLookAndFeel extends CheckPoint {

    private final Window master;

    public ApplyingLookAndFeel(Window master) {
        this.master = master;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
            String className = App.properties.getProperty("settings.lookAndFeel.className");
            UIManager.LookAndFeelInfo lookAndFeelInfo = null;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getClassName().equals(className)) {
                    lookAndFeelInfo = info;
                    break;
                }
            }
            if (lookAndFeel.getID().equals("Metal")) {
                String boldMetal = App.properties.getProperty("settings.lookAndFeel.swing.boldMetal");
                if (boldMetal != null) {
                    UIManager.put(Settings.BOLD_METAL, Boolean.parseBoolean(boldMetal));
                }
            }
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(className);
                    SwingUtilities.updateComponentTreeUI(master);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return buildMessage(Level.FINEST, "actualLookAndFeel", lookAndFeelInfo);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
