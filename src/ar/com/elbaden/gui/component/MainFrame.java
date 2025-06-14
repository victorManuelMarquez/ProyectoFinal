package ar.com.elbaden.gui.component;

import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        // ajustes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // componentes
        setJMenuBar(new JMenuBar());
        JMenu fileMenu = new JMenu(App.MESSAGES.getString("file"));
        JMenuItem exitMenuItem = new JMenuItem(createExitAction());

        // instalando componentes
        fileMenu.add(exitMenuItem);
        getJMenuBar().add(fileMenu);

        // eventos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showClosingDialog();
            }
        });
    }

    private AbstractAction createExitAction() {
        return new AbstractAction(App.MESSAGES.getString("exit")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showClosingDialog();
            }
        };
    }

    private void showClosingDialog() {
        int response = ClosingDialog.createAndShow(this);
        if (response == JOptionPane.OK_OPTION) {
            dispose();
        }
    }

    private void installMnemonics() {
        List<Character> characters = new ArrayList<>();
        // menu principal
        JMenuBar menuBar = getJMenuBar();
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            findMnemonicForItems(menuBar.getMenu(i), characters);
        }
    }

    private void findMnemonicForItems(JMenuItem item, List<Character> characters) {
        if (item instanceof JMenu menu) {
            for (int i = 0; i < menu.getItemCount(); i++) {
                findMnemonicForItems(menu.getItem(i), characters);
            }
        }
        if (item.getMnemonic() == KeyEvent.VK_UNDEFINED) {
            int pos = 0;
            int index = findCharIndex(item.getText(), characters, pos);
            if (index != -1) {
                char value = item.getText().charAt(index);
                if (Character.isUpperCase(value)) {
                    item.setMnemonic(value);
                } else {
                    item.setMnemonic(Character.toUpperCase(value));
                    item.setDisplayedMnemonicIndex(index);
                }
            }
        }
    }

    private int findCharIndex(String value, List<Character> characters, int pos) {
        if (pos == value.length()) {
            return -1;
        }
        char car = value.charAt(pos);
        if (Character.isUpperCase(car)) {
            if (characters.contains(car)) {
                int lastIndex = value.lastIndexOf(Character.toLowerCase(car));
                if (lastIndex != -1) {
                    characters.add(Character.toLowerCase(car));
                    return lastIndex;
                }
            } else {
                characters.add(car);
                return pos;
            }
        }
        if (characters.contains(car)) {
            findCharIndex(value, characters, pos + 1);
        } else {
            characters.add(car);
        }
        return pos;
    }

    public static void createAndShow(String title) {
        MainFrame frame = new MainFrame(title);
        Settings.updateAllFonts(frame);
        frame.installMnemonics();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
