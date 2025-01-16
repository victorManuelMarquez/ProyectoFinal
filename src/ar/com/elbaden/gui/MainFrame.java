package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
            JFrame frame = (JFrame) SwingUtilities.getRoot(itemExit);
            mainEvents.windowClosing(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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

    static class MainEvents extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (e.getSource() instanceof JFrame frame)
                frame.dispose();
        }
    }

}
