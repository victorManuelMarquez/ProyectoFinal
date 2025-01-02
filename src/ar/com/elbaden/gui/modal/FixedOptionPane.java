package ar.com.elbaden.gui.modal;

import javax.swing.*;

public class FixedOptionPane extends JOptionPane {

    @Override
    public int getMaxCharactersPerLineCount() {
        return 100;
    }

}
