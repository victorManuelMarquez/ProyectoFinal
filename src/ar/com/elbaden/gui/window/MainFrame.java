package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

public class MainFrame extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    private MainFrame(String title) throws HeadlessException {
        super(title);
        // componentes
        setJMenuBar(new JMenuBar());
        JMenu fileMenu = new JMenu(App.messages.getString("file"));
        JMenuItem exitMenuItem = new JMenuItem(App.messages.getString("exit"));

        // instalando componentes
        fileMenu.add(exitMenuItem);
        getJMenuBar().add(fileMenu);

        // ajustes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // eventos
        WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showClosingDialog();
            }
        };
        addWindowListener(windowAdapter);
        exitMenuItem.addActionListener(_ -> showClosingDialog());
    }

    private void showClosingDialog() {
        int result = ClosingDialog.createAndShow(this);
        if (result == JOptionPane.OK_OPTION) {
            dispose();
        }
    }

    public static void createAndShow() {
        try {
            MainFrame frame = new MainFrame(App.messages.getString("mainFrame.title"));
            Settings.updateFont(frame);
            MnemonicFinder.automaticMnemonics(frame);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
