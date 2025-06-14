package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;

public class ClosingDialog extends ModalDialog {

    private int response;

    private ClosingDialog(Window owner, String title) {
        super(owner, title);
        // ajustes
        setResizable(false);

        // variables
        FlowLayout flowLayout = new FlowLayout(FlowLayout.TRAILING);
        int hGap = flowLayout.getHgap();
        int vGap = flowLayout.getVgap();
        flowLayout.setVgap(0);
        int margin = 15;

        // componentes
        JPanel mainPanel = new JPanel(new BorderLayout(hGap, margin));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        JLabel messageLabel = new JLabel(App.MESSAGES.getString("closingDialog.message"));
        messageLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(vGap, hGap, vGap, hGap));
        JPanel inputPanel = new JPanel(flowLayout);
        JButton exitBtn = new JButton(App.MESSAGES.getString("closingDialog.exitOption"));
        JButton cancelBtn = new JButton(App.MESSAGES.getString("closingDialog.cancelOption"));

        // instalando componentes
        getRootPane().setDefaultButton(exitBtn);
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(messageLabel);
        inputPanel.add(exitBtn);
        inputPanel.add(cancelBtn);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);

        // eventos
        exitBtn.addActionListener(_ -> {
            response = JOptionPane.OK_OPTION;
            dispose();
        });
        cancelBtn.addActionListener(_ -> {
            response = JOptionPane.CANCEL_OPTION;
            dispose();
        });
    }

    public static int createAndShow(Window owner) {
        ClosingDialog dialog = new ClosingDialog(owner, App.MESSAGES.getString("closingDialog.title"));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.response;
    }

}
