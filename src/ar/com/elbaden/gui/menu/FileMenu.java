package ar.com.elbaden.gui.menu;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_F4;
import static java.awt.event.KeyEvent.VK_S;

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
        KeyStroke settingsKeyStroke;
        settingsKeyStroke = KeyStroke.getKeyStroke(VK_S, CTRL_DOWN_MASK | ALT_DOWN_MASK);
        settingsItem.setAccelerator(settingsKeyStroke);

        AbstractAction showSettingsDialog = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fixme: replantear toda la lógica ante las fallas descubiertas
                System.out.println("Se muestra el dialogo de configuración");
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(settingsKeyStroke, localSettings);
        getActionMap().put(localSettings, showSettingsDialog);

        settingsItem.addActionListener(showSettingsDialog);

        KeyStroke exitKeyStroke;
        exitKeyStroke = KeyStroke.getKeyStroke(VK_F4, ALT_DOWN_MASK);
        exitItem.setAccelerator(exitKeyStroke);

        AbstractAction showExitDialog = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window root = SwingUtilities.windowForComponent(getParent());
                root.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(exitKeyStroke, localExit);
        getActionMap().put(localExit, showExitDialog);

        exitItem.addActionListener(showExitDialog);
    }

}
