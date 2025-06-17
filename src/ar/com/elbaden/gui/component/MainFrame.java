package ar.com.elbaden.gui.component;

import ar.com.elbaden.gui.MnemonicFinder;
import ar.com.elbaden.gui.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private MainFrame(String title) throws HeadlessException {
        super(title);
        // localización
        String exitString = App.MESSAGES.getString("exit");
        String fileString = App.MESSAGES.getString("file");

        // ajustes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // componentes
        setJMenuBar(new JMenuBar());
        JMenu fileMenu = new JMenu(fileString);
        JMenuItem exitMenuItem = new JMenuItem(createExitAction(exitString));

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

    private AbstractAction createExitAction(String name) {
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showClosingDialog();
            }
        };
    }

    private void showClosingDialog() {
        String confirm = App.defaults().getOrDefault(Settings.CONFIRM_EXIT_KEY, "true").toString();
        if (Boolean.parseBoolean(confirm)) {
            int response = ClosingDialog.createAndShow(this);
            if (response == JOptionPane.OK_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    public static void createAndShow(String title) {
        MainFrame frame = new MainFrame(title);
        MnemonicFinder.automaticMnemonics(frame.getJMenuBar());
        Settings.updateAllFonts(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
