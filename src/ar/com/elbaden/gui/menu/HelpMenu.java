package ar.com.elbaden.gui.menu;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class HelpMenu extends JMenu {

    public HelpMenu() throws MissingResourceException {
        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        // contenido local
        String localText = messages.getString("menu.help");
        String localAbout = messages.getString("menu.about");

        setText(localText);

        // componentes
        JMenuItem aboutItem = new JMenuItem(localAbout);

        // instalando los componentes en el menú
        add(aboutItem);

        // eventos
        aboutItem.addActionListener(_ -> {
            // agregar un nuevo dialogo
            System.out.println("mostrar un dialogo de acerca del programa.");
        });
    }

}
