package ar.com.elbaden.gui.component;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.util.*;

public class FontFamilyComboBoxModel extends DefaultComboBoxModel<String> {

    private final Map<String, List<String>> familyMap;
    private final String generalFontFamily;

    public FontFamilyComboBoxModel() {
        familyMap = new TreeMap<>();
        generalFontFamily = App.settings.generalFontFamily();
        loadFamilies();
    }

    private void loadFamilies() {
        List<String> allFamilies = App.settings.getFontFamilies();
        String sans = "Sans";
        String serif = "Serif";
        String fontFamily;
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
        // copia de las claves
        Set<String> stringSet = Set.copyOf(familyMap.keySet());
        // descarto la clave para reemplazarla por el único ítem que contiene
        stringSet.forEach(k -> {
            List<String> list = familyMap.get(k);
            if (list.size() == 1) {
                String unique = list.getFirst();
                list.clear();
                familyMap.remove(k);
                familyMap.put(unique, list);
            }
        });
        // agrego las claves que son el nombre de cada fuente o familia de fuentes al modelo
        familyMap.forEach((k, v) -> {
            addElement(k);
            if (k.equals(generalFontFamily) || v.contains(generalFontFamily)) {
                setSelectedItem(k);
            }
        });
    }

    public Map<String, List<String>> getFamilyMap() {
        return Collections.unmodifiableMap(familyMap);
    }

    public String getGeneralFontFamily() {
        return generalFontFamily;
    }

}
