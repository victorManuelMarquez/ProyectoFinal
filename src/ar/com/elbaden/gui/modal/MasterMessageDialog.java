package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;

public class MasterMessageDialog extends MasterDialog {

    private Icon messageIcon;

    public MasterMessageDialog(Window owner, String title, int legacyIcon) {
        super(owner, title);
        boolean decorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
        switch (legacyIcon) {
            case JOptionPane.ERROR_MESSAGE -> {
                setMessageIcon(UIManager.getIcon("OptionPane.errorIcon"));
                if (decorations) getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
            }
            case JOptionPane.INFORMATION_MESSAGE -> {
                setMessageIcon(UIManager.getIcon("OptionPane.informationIcon"));
                if (decorations) getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
            }
            case JOptionPane.QUESTION_MESSAGE -> {
                setMessageIcon(UIManager.getIcon("OptionPane.questionIcon"));
                if (decorations) getRootPane().setWindowDecorationStyle(JRootPane.QUESTION_DIALOG);
            }
            case JOptionPane.WARNING_MESSAGE -> {
                setMessageIcon(UIManager.getIcon("OptionPane.warningIcon"));
                if (decorations) getRootPane().setWindowDecorationStyle(JRootPane.WARNING_DIALOG);
            }
        }
    }

    public Icon getMessageIcon() {
        return messageIcon;
    }

    public void setMessageIcon(Icon messageIcon) {
        this.messageIcon = messageIcon;
    }

}
