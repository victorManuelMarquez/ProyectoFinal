package ar.com.elbaden.gui.modal;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;

public final class ClosingDialog extends MasterDialog {

    private EnumSet<Options> flag = EnumSet.noneOf(Options.class);

    private ClosingDialog(Component component, String title) {
        super(component, title);
        String localCheckBox = "No preguntar de nuevo";
        String localMsg = "¿Está seguro que desea salir?";
        String localOk = "Aceptar";
        String localCancel = "Cancelar";

        JCheckBox checkBox = new JCheckBox(localCheckBox);
        JButton okButton = new JButton(localOk);
        JButton cancelButton = new JButton(localCancel);
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");

        getContentPane().add(new JLabel(icon), BorderLayout.WEST);

        JPanel messagePanel = new JPanel(new GridLayout(0, 1, 8, 8));
        getContentPane().add(messagePanel);

        messagePanel.add(new JLabel(localMsg));
        messagePanel.add(checkBox);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        okButton.addActionListener(_ -> {
            if (checkBox.isSelected()) {
                flag = EnumSet.of(Options.SAY_YES, Options.SKIP);
            } else {
                flag = EnumSet.of(Options.SAY_YES);
            }
            dispose();
        });

        cancelButton.addActionListener(_ -> {
            if (checkBox.isSelected()) {
                flag = EnumSet.of(Options.SAY_NO, Options.SKIP);
            } else {
                flag = EnumSet.of(Options.SAY_NO);
            }
            dispose();
        });
    }

    public static EnumSet<Options> createAndShow(Component component) {
        String localTitle = "Confirme la acción";
        ClosingDialog dialog = new ClosingDialog(component, localTitle);
        dialog.pack();
        dialog.setLocationRelativeTo(component);
        dialog.setVisible(true);
        return dialog.flag;
    }

    public enum Options {
        SAY_YES,
        SAY_NO,
        SKIP
    }

}
