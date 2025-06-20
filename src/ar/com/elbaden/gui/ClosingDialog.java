package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ClosingDialog extends ModalDialog {

    private static final Logger LOGGER = Logger.getLogger(ClosingDialog.class.getName());
    private int exitResponse = JOptionPane.DEFAULT_OPTION;

    private ClosingDialog(Window owner, String title) {
        super(owner, title);
        // localización
        ResourceBundle messages = ResourceBundle.getBundle(App.BUNDLE_NAME);
        LOGGER.setResourceBundle(messages);

        // variables
        FlowLayout flowLayout = new FlowLayout(FlowLayout.TRAILING);
        int hGap = flowLayout.getHgap();
        int vGap = flowLayout.getVgap();
        flowLayout.setHgap(0);
        flowLayout.setVgap(0);
        int margin = 15;
        int row = 0;

        // componentes
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(vGap, hGap, vGap, hGap));
        JLabel icon = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        JLabel message = new JLabel(messages.getString("closingDialog.message"));
        JCheckBox noShowAgain = new JCheckBox(messages.getString("closingDialog.doNotAskAgain"));
        JPanel inputPanel = new JPanel(flowLayout);
        JButton exitBtn = new JButton(messages.getString("exit"));
        JButton cancelBtn = new JButton(messages.getString("cancel"));

        // instalando componentes
        inputPanel.add(exitBtn);
        inputPanel.add(Box.createHorizontalStrut(margin));
        inputPanel.add(cancelBtn);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(vGap, hGap, vGap, hGap);
        gbc.gridy = row;
        gbc.weightx = 1.0;
        mainPanel.add(icon, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(message, gbc);
        row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.ipady = vGap;
        mainPanel.add(noShowAgain, gbc);
        row++;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = row;
        gbc.ipady = 0;
        mainPanel.add(inputPanel, gbc);
        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(exitBtn);

        // ajustes
        setResizable(false);

        // eventos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                saveConfirmValue();
            }
        });
        noShowAgain.addActionListener(_ -> {
            String key = Settings.BASE_KEY + ".confirmExit";
            App.setDefault(key, !noShowAgain.isSelected());
        });
        SwingUtilities.invokeLater(exitBtn::requestFocusInWindow);
        exitBtn.addActionListener(_ -> {
            exitResponse = JOptionPane.OK_OPTION;
            dispose();
        });
        cancelBtn.addActionListener(_ -> {
            exitResponse = JOptionPane.CANCEL_OPTION;
            dispose();
        });
    }

    private void saveConfirmValue() {
        try {
            File appDir = new File(System.getProperty("user.home"), App.FOLDER_NAME);
            File xsd = new File(appDir, Settings.XSD_FILE_NAME);
            File xsl = new File(appDir, Settings.XSL_FILE_NAME);
            File xml = new File(appDir, Settings.XML_FILE_NAME);
            Settings.save(App.getProperties(), xsd, xsl, xml);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static int createAndShow(Window origin, String title) {
        ClosingDialog dialog = new ClosingDialog(origin, title);
        Settings.applyFont(App.getProperties(), dialog, 0);
        MnemonicFinder.automaticMnemonics(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(origin);
        dialog.setVisible(true);
        return dialog.exitResponse;
    }

}
