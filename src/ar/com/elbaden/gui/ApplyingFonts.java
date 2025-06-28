package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ApplyingFonts extends CheckPoint {

    private final Window master;

    public ApplyingFonts(Window master) {
        this.master = master;
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
            int total = Settings.updateExclusiveFonts(lookAndFeel);
            total += Settings.updateFont(master);
            SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(master));
            return buildMessage("updatedFonts", total);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
