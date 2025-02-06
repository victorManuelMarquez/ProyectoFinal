package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;

abstract class MasterDialog extends JDialog {

    public final Dimension preferredMaxDimensions = new Dimension(640, 480);

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
