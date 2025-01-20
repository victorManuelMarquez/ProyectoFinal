package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;

class MasterDialog extends JDialog {

    public MasterDialog(Component component, String title) {
        super(component instanceof Window window ? window : null);
        setTitle(title);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        setUndecorated(JRootPane.NONE != getRootPane().getWindowDecorationStyle());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
    }

}
