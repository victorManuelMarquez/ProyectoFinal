package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;

class MasterDialog extends JDialog {

    public MasterDialog(Frame owner, String title) {
        super(owner, title);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        setUndecorated(JRootPane.NONE != getRootPane().getWindowDecorationStyle());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
    }

}
