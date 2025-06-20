package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;

public abstract class ModalDialog extends JDialog {

    protected ModalDialog(Window owner, String title) {
        super(owner, title);
        // ajustes
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        }
    }

}
