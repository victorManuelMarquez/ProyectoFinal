package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

public class MainFrame extends JFrame {

    public static ResourceBundle messages;

    private MainFrame(String title) throws HeadlessException {
        super(title);
        // componentes
        setJMenuBar(new JMenuBar());
        JMenu fileMenu = new JMenu(messages.getString("file"));
        JMenuItem exitItem = new JMenuItem(createExitAction(messages.getString("exit")));

        // instalando componentes
        fileMenu.add(exitItem);
        getJMenuBar().add(fileMenu);

        // ajustes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // eventos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showClosingDialog();
            }
        });
    }

    private AbstractAction createExitAction(String name) {
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showClosingDialog();
            }
        };
    }

    private void showClosingDialog() {
        String confirm = App.getProperties().getOrDefault("settings.confirmExit", "true").toString();
        if (Boolean.parseBoolean(confirm)) {
            String title = messages.getString("closingDialog.title");
            int response = ClosingDialog.createAndShow(this, title);
            if (response == JOptionPane.OK_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    public static void createAndShow() {
        messages = ResourceBundle.getBundle(App.BUNDLE_NAME);
        MainFrame frame = new MainFrame(messages.getString("mainFrame.title"));
        Settings.applyFont(App.getProperties(), frame, 0);
        MnemonicFinder.automaticMnemonics(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
