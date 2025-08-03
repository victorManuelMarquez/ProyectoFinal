package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Settings extends Properties {

    private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());

    public static final String APP_FOLDER = ".baden";
    public static final String BASE_KEY = "settings";
    public static final int DEFAULT = 0;
    public static final int CUSTOM = -1;

    private final List<String> fontFamilies;

    static {
        LOGGER.setParent(Logger.getLogger(App.class.getName()));
    }

    public Settings() {
        loadDefaults();
        fontFamilies = new ArrayList<>();
    }

    public File getAppFolder() {
        return new File(System.getProperty("user.home"), APP_FOLDER);
    }

    public File getLog() {
        return new File(getAppFolder(), "log.txt");
    }

    public File getPropertiesFile() {
        return new File(getAppFolder(), BASE_KEY + ".properties");
    }

    private String createKey(String key) {
        return BASE_KEY + "." + key;
    }

    public void loadDefaults() {
        defaults = new Properties();

        // fuentes
        UIDefaults uiDefaults = UIManager.getDefaults();
        Enumeration<Object> keys = uiDefaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Font font = uiDefaults.getFont(key);
            if (font != null) {
                defaults.put(createKey(key.toString()).concat(".family"), font.getFamily());
                defaults.put(createKey(key.toString()).concat(".size"), Integer.toString(font.getSize()));
                defaults.put(createKey(key.toString()).concat(".style"), Integer.toString(font.getStyle()));
            }
        }

        // tema
        LookAndFeel laf = UIManager.getLookAndFeel();
        String lafKey = "lookAndFeel";
        if ("Metal".equals(laf.getID())) {
            String metalKey = "swing.boldMetal";
            boolean boldMetal = !laf.getDefaults().containsKey(metalKey) || UIManager.getBoolean(metalKey);
            defaults.put(createKey(metalKey), Boolean.toString(boldMetal));
        }
        defaults.put(createKey("showClosingDialog"), Boolean.toString(true));
        defaults.put(createKey(lafKey).concat(".className"), laf.getClass().getName());
        defaults.put(createKey(lafKey).concat(".id"), laf.getID());

        // cargo los valores a esta configuración
        putAll(defaults);
    }

    public String loadAvailableFontFamilies() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (!ge.isHeadlessInstance()) {
            String[] families = ge.getAvailableFontFamilyNames(Locale.getDefault());
            fontFamilies.addAll(List.of(families));
        }
        String pattern = App.messages.getString("settings.fontFamily.totalLoaded");
        return MessageFormat.format(pattern, fontFamilies.size());
    }

    public String load() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getPropertiesFile())) {
            load(inputStream);
            return App.messages.getString("settings.loaded");
        }
    }

    public String save() throws IOException {
        String commentary = App.messages.getString("settings.commentary");
        try (FileOutputStream outputStream = new FileOutputStream(getPropertiesFile())) {
            store(outputStream, commentary);
            return App.messages.getString("settings.saved");
        }
    }

    public int count() {
        return keySet().size();
    }

    public String changeLook(Window origin) throws Exception {
        String id = getProperty("settings.lookAndFeel.id");
        String className = getProperty("settings.lookAndFeel.className");
        if (UIManager.getLookAndFeel().getClass().getName().equals(className)) {
            String pattern = App.messages.getString("alreadyLookAndFeelSet");
            return MessageFormat.format(pattern, id);
        }
        if (SwingUtilities.isEventDispatchThread()) {
            updateLookAndFeel(className, origin);
        } else {
            // actualización segura
            SwingUtilities.invokeAndWait(() -> {
                try {
                    updateLookAndFeel(className, origin);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        String pattern = App.messages.getString("lookAndFeelSet");
        return MessageFormat.format(pattern, id);
    }

    private void updateLookAndFeel(String className, Window origin) throws Exception {
        UIManager.setLookAndFeel(className);
        SwingUtilities.updateComponentTreeUI(origin);
    }

    public String updateFonts(Component source) {
        StringBuilder output = new StringBuilder();
        if (source instanceof JMenuItem menuItem) {
            for (MenuElement element : menuItem.getSubElements()) {
                output.append(updateFonts((Component) element));
            }
        } else if (source instanceof Container container) {
            for (Component component : container.getComponents()) {
                output.append(updateFonts(component));
            }
        }
        String className = findSwingClassName(source.getClass());
        if (className == null) {
            return null;
        } else {
            String baseName = className.replace("javax.swing.J", "");
            Predicate<String> containKey = k -> k.contains(baseName) && k.endsWith("font.family");
            Stream<Object> filtered = keySet().stream().filter(k -> containKey.test(k.toString()));
            Optional<Object> familyKey = filtered.findFirst();
            if (familyKey.isPresent()) {
                String stringFamilyKey = familyKey.get().toString();
                applyFont(source, stringFamilyKey);
                String pattern = App.messages.getString("componentFontUpdated");
                String family = getProperty(stringFamilyKey);
                output.append(MessageFormat.format(pattern, family, baseName)).append(System.lineSeparator());
            }
        }
        return output.toString();
    }

    private void applyFont(Component source, String familyKey) {
        Font font = new Font(getProperty(familyKey), Font.PLAIN, 12);
        String styleKey = familyKey.replace(".family", ".style");
        switch (Integer.parseInt(getProperty(styleKey))) {
            case Font.BOLD -> font = font.deriveFont(Font.BOLD);
            case Font.ITALIC -> font = font.deriveFont(Font.ITALIC);
            case Font.BOLD|Font.ITALIC -> font = font.deriveFont(Font.BOLD|Font.ITALIC);
        }
        String sizeKey = familyKey.replace(".family", ".size");
        font = font.deriveFont(Float.parseFloat(getProperty(sizeKey)));
        if (!isSameFont(source.getFont(), font)) {
            source.setFont(font);
        }
    }

    private String findSwingClassName(Class<?> clazz) {
        if (clazz.getName().startsWith("javax.swing.J")) {
            return clazz.getName();
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null) {
            return null;
        } else {
            return findSwingClassName(superClass);
        }
    }

    private boolean isSameFont(Font original, Font generated) {
        if (original == null) {
            return false;
        }
        boolean familyMatch = original.getFamily().equals(generated.getFamily());
        boolean styleMatch = original.getStyle() == generated.getStyle();
        boolean sizeMatch = original.getSize() == generated.getSize();
        return familyMatch && styleMatch && sizeMatch;
    }

    public List<String> fontSizeKeys() {
        Set<Object> keys = App.settings.keySet();
        Stream<Object> keyStream = keys.stream().filter(k -> k.toString().endsWith(".size"));
        return keyStream.map(Object::toString).toList();
    }

    public List<String> fontFamilyKeys() {
        Set<Object> keys = App.settings.keySet();
        Stream<Object> keyStream = keys.stream().filter(k -> k.toString().endsWith(".family"));
        return keyStream.map(Object::toString).toList();
    }

    private boolean containsAll(Map<String, String> values) {
        boolean contains = false;
        for (String key : values.keySet()) {
            contains = containsKey(key);
            if (contains) {
                contains = getProperty(key).equals(values.get(key));
            }
            if (!contains) {
                break;
            }
        }
        return contains;
    }

    public int generalFontSize() {
        List<String> sizeKeys = fontSizeKeys();
        Map<String, String> defaultFonts = new HashMap<>();
        sizeKeys.forEach(k -> defaultFonts.put(k, defaults.getProperty(k)));
        Set<Integer> allSizes = new TreeSet<>();
        sizeKeys.forEach(k -> {
            try {
                int value = Integer.parseInt(getProperty(k));
                allSizes.add(value);
            } catch (RuntimeException e) {
                LOGGER.severe(e.getMessage());
            }
        });
        return containsAll(defaultFonts) ? DEFAULT : allSizes.size() == 1 ? allSizes.stream().findFirst().get(): CUSTOM;
    }

    public String generalFontFamily() {
        List<String> familyKeys = fontFamilyKeys();
        Set<String> families = new TreeSet<>();
        familyKeys.forEach(k -> families.add(getProperty(k)));
        if (families.size() == 1) {
            return families.stream().findFirst().get();
        } else {
            return App.messages.getString("multipleValues");
        }
    }

    public List<String> getFontFamilies() {
        return Collections.unmodifiableList(fontFamilies);
    }

}
