package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;

abstract class MasterDialog extends JDialog {

    public MasterDialog(Window owner, String title) {
        super(owner, title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        }
    }

}
