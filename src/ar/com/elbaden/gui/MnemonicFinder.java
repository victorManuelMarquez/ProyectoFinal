package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MnemonicFinder {

    private final List<Character> characters;

    private MnemonicFinder() {
        characters = new ArrayList<>();
    }

    public static void automaticMnemonics(Component origin) {
        MnemonicFinder finder = new MnemonicFinder();
        finder.recursiveSearch(origin, finder.characters);
    }

    public void recursiveSearch(Component origin, List<Character> characters) {
        if (origin instanceof JMenuItem item) {
            for (MenuElement element : item.getSubElements()) {
                recursiveSearch(element.getComponent(), characters);
            }
        } else if (origin instanceof Container container) {
            for (Component component : container.getComponents()) {
                recursiveSearch(component, characters);
            }
        }
        if (origin instanceof AbstractButton button) {
            String text = button.getText();
            if (text != null && !text.isBlank() && button.getMnemonic() == KeyEvent.VK_UNDEFINED) {
                int pos = text.length() - 1;
                int index = findCharIndex(text, characters, pos);
                if (index != -1) {
                    char letter = text.charAt(index);
                    button.setMnemonic(letter);
                    button.setDisplayedMnemonicIndex(index);
                }
            }
        }
    }

    public int findCharIndex(String value, List<Character> characters, int pos) {
        if (pos < 0) {
            return -1;
        }
        char letter = value.charAt(pos);
        if (characters.contains(letter)) {
            int index = value.indexOf(letter);
            if (index != -1) {
                return index;
            } else {
                return findCharIndex(value, characters, pos - 1);
            }
        } else if (Character.isUpperCase(letter)) {
            characters.add(letter);
            return pos;
        } else {
            return findCharIndex(value, characters, pos - 1);
        }
    }

}
