package ar.com.elbaden.gui.component;

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
        String message = App.MESSAGES.getString("closingDialog.message");
        String title = App.MESSAGES.getString("closingDialog.title");
        int optionType = JOptionPane.YES_NO_OPTION;
        int icon = JOptionPane.QUESTION_MESSAGE;
        int response = JOptionPane.showConfirmDialog(this, message, title, optionType, icon);
        if (response == JOptionPane.OK_OPTION) {
            dispose();
        }
    }

    public static void createAndShow(String title) {
        MainFrame frame = new MainFrame(title);
        Settings.updateAllFonts(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
