package ar.com.elbaden.gui.component;

import javax.swing.*;
import java.awt.*;

public abstract class ModalDialog extends JDialog {

    public ModalDialog(Window owner, String title) {
        super(owner, title);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        }
    }

}
