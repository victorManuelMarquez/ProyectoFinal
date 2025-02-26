package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MnemonicFinder {

    public static void findMnemonics(JComponent origin) {
        StringBuilder letters = new StringBuilder();
        for (Component component : origin.getComponents()) {
            System.out.println(component.getClass().getSimpleName());
            if (component instanceof AbstractButton button) {
                if (button.getMnemonic() == KeyEvent.VK_UNDEFINED) {
                    boolean mnemonicSet = false;
                    int index = 0;
                    char[] chars = button.getText().toCharArray();
                    while (index < chars.length && !mnemonicSet) {
                        if (letters.toString().indexOf(chars[index]) == -1) {
                            letters.append(chars[index]);
                            button.setMnemonic(mnemonicValue(chars[index]));
                            mnemonicSet = true;
                        }
                        index++;
                    }
                }
            }
        }
    }

    public static int mnemonicValue(char value) {
        return Character.getNumericValue(value) + 55;
    }

}
