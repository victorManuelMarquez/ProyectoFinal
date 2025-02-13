package ar.com.elbaden.gui.menu;

import ar.com.elbaden.gui.modal.SettingsDialog;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class FileMenu extends JMenu {

    public FileMenu() throws MissingResourceException {
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localText = messages.getString("menu.file");
        String localSettings = messages.getString("menu.settings");
        String localExit = messages.getString("menu.exit");

        setText(localText);

        // componentes
        JMenuItem settingsItem = new JMenuItem(localSettings);
        JMenuItem exitItem = new JMenuItem(localExit);

        // instalando los componentes en el menú
        add(settingsItem);
        addSeparator();
        add(exitItem);

        // eventos
        settingsItem.addActionListener(_ -> {
            Window root = SwingUtilities.windowForComponent(this);
            SettingsDialog.createAndShow(root, localSettings);
        });

        exitItem.addActionListener(_ -> {
            Window root = SwingUtilities.windowForComponent(this);
            root.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
        });
    }

}
