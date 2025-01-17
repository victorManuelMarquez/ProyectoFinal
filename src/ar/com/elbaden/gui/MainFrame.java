package ar.com.elbaden.gui;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.modal.ClosingDialog;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumSet;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        installComponents();
    }

    private void installComponents() {
        installMainMenu();
    }

    public void installMainMenu() {
        JMenuBar mainMenu = new JMenuBar();
        setJMenuBar(mainMenu);

        String localFile = "Archivo";
        JMenu fileMenu = new JMenu(localFile);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        getJMenuBar().add(fileMenu);

        fileMenu.addSeparator();

        String localExit = "Salir";
        JMenuItem itemExit = new JMenuItem(localExit);
        itemExit.setMnemonic(KeyEvent.VK_X);
        itemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        fileMenu.add(itemExit);

        MainEvents mainEvents = new MainEvents();
        addWindowListener(mainEvents);

        itemExit.addActionListener(_ -> {
            JFrame root = (JFrame) SwingUtilities.getRoot(getJMenuBar());
            mainEvents.windowClosing(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
        });
    }

    public static void createAndShow() {
        String localTitle = "Bienvenido";
        MainFrame frame = new MainFrame(localTitle);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static protected final class MainEvents extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            if (e.getSource() instanceof JFrame frame) {
                Object hide = App.properties.getProperty(Settings.KEY_HIDE_CLOSING_DIALOG);
                if (hide != null && Boolean.parseBoolean(hide.toString())) {
                    frame.dispose();
                    return;
                }
                EnumSet<ClosingDialog.Options> flag = ClosingDialog.createAndShow(frame);
                if (flag.contains(ClosingDialog.Options.SKIP)) {
                    App.properties.setProperty(Settings.KEY_HIDE_CLOSING_DIALOG, Boolean.toString(true));
                    Settings.storeExternal(frame);
                }
                if (flag.contains(ClosingDialog.Options.SAY_YES))
                    frame.dispose();
            }
        }

    }

}
