package ar.com.elbaden.gui;

import ar.com.elbaden.error.ResourceBundleException;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static java.awt.event.WindowEvent.WINDOW_CLOSING;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException, ResourceBundleException {
        super(title);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(MAXIMIZED_BOTH)) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        ResourceBundle messages;
        try {
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        } catch (MissingResourceException e) {
            throw new ResourceBundleException(e);
        }

        // contenido local
        String localFile = messages.getString("menu.file");
        String localExit = messages.getString("menu.exit");

        // Menú principal
        setJMenuBar(new JMenuBar());

        JMenu fileMenu = new JMenu(localFile);
        getJMenuBar().add(fileMenu);

        fileMenu.addSeparator();

        JMenuItem exitOption = new JMenuItem(localExit);
        fileMenu.add(exitOption);

        // eventos
        WindowEvents events = new WindowEvents();
        addWindowListener(events);

        exitOption.addActionListener(_ -> events.windowClosing(new WindowEvent(this, WINDOW_CLOSING)));
    }

    public static void createAndShow(String title) {
        try {
            MainFrame frame = new MainFrame(title);
            frame.pack();
            frame.setMinimumSize(frame.getSize());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (ResourceBundleException e) {
            throw new RuntimeException(e);
        }
    }

    static class WindowEvents extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            // aquí tengo que poner un diálogo para confirmar el cierre
            e.getWindow().dispose();
        }

    }

}
