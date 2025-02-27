package ar.com.elbaden.gui;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.menu.FileMenu;
import ar.com.elbaden.gui.menu.HelpMenu;
import ar.com.elbaden.gui.modal.ClosingDialog;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }

        if (Toolkit.getDefaultToolkit().isFrameStateSupported(MAXIMIZED_BOTH)) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        // Menú principal
        setJMenuBar(new JMenuBar());

        FileMenu fileMenu = new FileMenu();
        getJMenuBar().add(fileMenu);

        HelpMenu helpMenu = new HelpMenu();
        getJMenuBar().add(helpMenu);

        MnemonicFinder.findMnemonics(getJMenuBar());
        MnemonicFinder.findMnemonics(fileMenu);

        // eventos
        WindowEvents events = new WindowEvents();
        addWindowListener(events);
    }

    public static void createAndShow(String title) {
        MainFrame frame = new MainFrame(title);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static class WindowEvents extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            String askForClosing = App.settings.getProperties().getProperty(Settings.KEY_ASK_FOR_CLOSING);
            boolean showDialog = Boolean.parseBoolean(askForClosing);
            if (showDialog) {
                int option = ClosingDialog.createAndShow(e.getWindow());
                if (option == JOptionPane.OK_OPTION) {
                    e.getWindow().dispose();
                }
            } else {
                e.getWindow().dispose();
            }
        }

    }

}
