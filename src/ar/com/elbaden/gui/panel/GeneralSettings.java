package ar.com.elbaden.gui.panel;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class GeneralSettings extends JPanel implements ActionListener {

    private boolean confirmExit;

    public GeneralSettings() throws MissingResourceException {
        super(new GridBagLayout());

        ResourceBundle messages;
        messages = ResourceBundle.getBundle(App.LOCALES_DIR);

        setName(messages.getString("literal.general_settings"));
        Border titledBorder = BorderFactory.createTitledBorder(getName());
        setBorder(titledBorder);

        // contenido local
        String localConfirm = messages.getString("checkbox.confirm_to_close");

        // componentes
        JCheckBox confirmToClose = new JCheckBox(localConfirm);
        String actualValue = App.settings.getProperties().getProperty(Settings.KEY_ASK_FOR_CLOSING);
        confirmExit = Boolean.parseBoolean(actualValue);
        confirmToClose.setSelected(confirmExit);

        // instalando componentes en el dialog
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.weightx = 1.0;

        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(confirmToClose, constraints);

        // eventos
        confirmToClose.addActionListener(_ -> confirmExit = confirmToClose.isSelected());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("apply".equals(e.getActionCommand()) || "apply&close".equals(e.getActionCommand())) {
            apply();
        }
    }

    private void apply() {
        String value = Boolean.toString(confirmExit);
        App.settings.getProperties().setProperty(Settings.KEY_ASK_FOR_CLOSING, value);
    }

}
