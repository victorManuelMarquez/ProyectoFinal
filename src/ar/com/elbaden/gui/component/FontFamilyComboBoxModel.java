package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.util.*;

public class FontFamilyComboBoxModel extends DefaultComboBoxModel<String> {

    private final Map<String, List<String>> familyMap;

    public FontFamilyComboBoxModel() {
        familyMap = new TreeMap<>();
        loadFamilies();
    }

    private void loadFamilies() {
        List<String> allFamilies = App.settings.getFontFamilies();
        String sans = "Sans";
        String serif = "Serif";
        String fontFamily;
        String generalFontFamily = App.settings.generalFontFamily();
        for (String family : allFamilies) {
            String[] split = family.split("\\s");
            if (split.length > 1) {
                if (family.contains(sans)) {
                    fontFamily = family.substring(0, family.indexOf(sans) + sans.length());
                } else if (family.contains(serif)) {
                    fontFamily = family.substring(0, family.indexOf(serif) + serif.length());
                } else {
                    fontFamily = split[0];
                }
                if (familyMap.containsKey(fontFamily)) {
                    List<String> list = familyMap.get(fontFamily);
                    list.add(family);
                } else {
                    familyMap.put(fontFamily, new ArrayList<>());
                }
            } else {
                familyMap.put(family, new ArrayList<>());
            }
        }
        familyMap.keySet().forEach(k -> {
            List<String> list = familyMap.get(k);
            if (list.size() == 1) {
                addElement(list.getFirst());
            } else {
                addElement(k);
            }
            if (list.contains(generalFontFamily)) {
                setSelectedItem(k);
            } else {
                setSelectedItem(generalFontFamily);
            }
        });
    }

    public Map<String, List<String>> getFamilyMap() {
        return Collections.unmodifiableMap(familyMap);
    }

}
