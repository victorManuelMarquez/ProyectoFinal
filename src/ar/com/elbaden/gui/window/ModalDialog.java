package ar.com.elbaden.gui.window;

import javax.swing.*;
import java.awt.*;

public class ModalDialog extends JDialog {

    public ModalDialog(Window owner, String title) {
        super(owner, title);
        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        }
    }

}
