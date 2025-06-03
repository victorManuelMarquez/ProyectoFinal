package ar.com.elbaden.main;

import ar.com.elbaden.gui.Settings;

import java.io.File;

public class App {

    public static void main(String[] args) {
        try {
            File outputDir = new File(System.getProperty("user.home"), ".baden");
            File xsdFile = new File(outputDir, "settings.xsd");
            File xmlFile = new File(outputDir, "settings.xml");
            Settings settings = new Settings();
            System.out.println(settings.restoreSchema(xsdFile, settings.createSchema()));
            System.out.println(settings.restoreDefaults(xmlFile));
            System.out.println(settings.load(xsdFile, xmlFile));
            System.out.println(settings.getClassTheme());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}
