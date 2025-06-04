package ar.com.elbaden.main;

import ar.com.elbaden.Settings;

import javax.swing.*;
import java.io.File;

public class App {

    public static final String FOLDER = ".baden";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File outputDir = new File(System.getProperty("user.home"), FOLDER);
                File xsdFile = new File(outputDir, "settings.xsd");
                File xmlFile = new File(outputDir, "settings.xml");
                Settings settings = new Settings();
                settings.restoreDefaults(xsdFile, xmlFile);
                settings.loadDocument(xsdFile, xmlFile);
                System.out.println(settings);
                System.out.println(settings.getTheme());
                System.out.println(settings.getFontsMap());
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

}
