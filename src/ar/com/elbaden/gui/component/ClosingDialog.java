package ar.com.elbaden.gui.component;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.SavingConfirmExit;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClosingDialog extends ModalDialog {

    private int response = JOptionPane.DEFAULT_OPTION;
    private boolean confirmExit = true;

    private ClosingDialog(Window owner, String title) {
        super(owner, title);
        // ajustes
        setResizable(false);

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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        JLabel messageLabel = new JLabel(App.MESSAGES.getString("closingDialog.message"));
        messageLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        JCheckBox noShowAgain = new JCheckBox(App.MESSAGES.getString("closingDialog.doNotAskAgain"));
        JPanel inputPanel = new JPanel(flowLayout);
        JButton exitBtn = new JButton(App.MESSAGES.getString("closingDialog.exitOption"));
        getRootPane().setDefaultButton(exitBtn);
        JButton cancelBtn = new JButton(App.MESSAGES.getString("closingDialog.cancelOption"));

        // instalando componentes
        inputPanel.add(exitBtn);
        inputPanel.add(Box.createHorizontalStrut(margin));
        inputPanel.add(cancelBtn);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(vGap, hGap, vGap, hGap);
        gbc.gridy = row;
        gbc.weightx = 1.0;
        mainPanel.add(iconLabel, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(messageLabel, gbc);
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

        // eventos
        WindowAdapter windowEvents = new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                saveConfirmExit();
            }
        };
        addWindowListener(windowEvents);
        noShowAgain.addActionListener(_ -> confirmExit = !noShowAgain.isSelected());
        SwingUtilities.invokeLater(exitBtn::requestFocusInWindow);
        exitBtn.addActionListener(_ -> {
            response = JOptionPane.OK_OPTION;
            dispose();
        });
        cancelBtn.addActionListener(_ -> {
            response = JOptionPane.CANCEL_OPTION;
            dispose();
        });
    }

    private void saveConfirmExit() {
        if (confirmExit) {
            return;
        }
        File outputDir = new File(System.getProperty("user.home"), App.FOLDER);
        File xsdFile = new File(outputDir, Settings.XSD_FILE_NAME);
        File xslFile = new File(outputDir, Settings.XSL_FILE_NAME);
        File xmlFile = new File(outputDir, Settings.XML_FILE_NAME);
        ExecutorService service = Executors.newSingleThreadExecutor();
        try (service) {
            Object result = service.submit(new SavingConfirmExit(xsdFile, xslFile, xmlFile, confirmExit)).get();
            App.LOGGER.info(String.format("%s = %s", Settings.CONFIRM_EXIT_KEY, result));
        } catch (Exception e) {
            App.LOGGER.severe(e.getMessage());
        } finally {
            service.shutdownNow();
        }
    }

    public static int createAndShow(Window owner) {
        ClosingDialog dialog = new ClosingDialog(owner, App.MESSAGES.getString("closingDialog.title"));
        Settings.updateAllFonts(dialog);
        MnemonicFinder.automaticMnemonics(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.response;
    }

}
