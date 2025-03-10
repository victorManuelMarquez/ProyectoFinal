package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class MnemonicFinder {

    private MnemonicFinder() {}

    public int mnemonicValue(char value) {
        return Character.getNumericValue(value) + 55;
    }

    public boolean ignoreThis(char character) {
        // caracteres que el mnemonic no puede resaltar correctamente (dependiendo de la fuente)
        List<Character> characters = List.of('g', 'j', 'p', 'q', 'y');
        if (!Character.isUpperCase(character)) {
            return characters.contains(character);
        } else return false;
    }

    public void setMnemonic(StringBuilder builder, AbstractButton button) {
        if (button.getMnemonic() == KeyEvent.VK_UNDEFINED) {
            boolean mnemonicSet = false;
            int index = 0;
            char[] chars = button.getText().toCharArray();
            while (index < chars.length && !mnemonicSet) {
                if (builder.toString().indexOf(chars[index]) == -1 && !ignoreThis(chars[index])) {
                    builder.append(chars[index]);
                    button.setMnemonic(mnemonicValue(chars[index]));
                    mnemonicSet = true;
                }
                index++;
            }
        }
    }

    public static void findMnemonics(JComponent origin) {
        MnemonicFinder finder = new MnemonicFinder();
        StringBuilder letters = new StringBuilder();
        if (origin instanceof JMenu menu) {
            for (Component component : menu.getMenuComponents()) {
                if (component instanceof AbstractButton button) {
                    finder.setMnemonic(letters, button);
                }
            }
        } else {
            for (Component component : origin.getComponents()) {
                if (component instanceof AbstractButton button) {
                    finder.setMnemonic(letters, button);
                }
            }
        }
    }

}
