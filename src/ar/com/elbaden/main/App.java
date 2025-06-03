package ar.com.elbaden.main;

import ar.com.elbaden.Settings;

import javax.swing.*;
import java.io.File;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File outputDir = new File(System.getProperty("user.home"), ".baden");
                File xsdFile = new File(outputDir, "settings.xsd");
                File xmlFile = new File(outputDir, "settings.xml");
                Settings settings = new Settings();
                settings.restoreDefaults(xsdFile, xmlFile);
                settings.loadDocument(xsdFile, xmlFile);
                System.out.println(settings.getTheme());
                System.out.println(settings);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

}
