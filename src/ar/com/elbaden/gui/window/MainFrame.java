package ar.com.elbaden.gui.window;

import ar.com.elbaden.gui.MnemonicFinder;
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
        JMenuItem settingsItem = new JMenuItem(App.messages.getString("settings"));
        JMenuItem exitMenuItem = new JMenuItem(App.messages.getString("exit"));

        // instalando componentes
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        getJMenuBar().add(fileMenu);

        // ajustes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // eventos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        settingsItem.addActionListener(_ -> SettingsDialog.createAndShow(this));
        exitMenuItem.addActionListener(_ -> close());
    }

    private void close() {
        String key = "settings.showClosingDialog";
        if (App.settings.containsKey(key)) {
            String bool = App.settings.getProperty(key);
            if (Boolean.parseBoolean(bool)) {
                int response = ClosingDialog.createAndShow(this);
                if (response != JOptionPane.OK_OPTION) {
                    return;
                }
            }
        }
        dispose();
    }

    public static void createAndShow() {
        try {
            MainFrame frame = new MainFrame(App.messages.getString("mainFrame.title"));
            App.settings.updateFonts(frame);
            MnemonicFinder.automaticMnemonics(frame);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (RuntimeException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
