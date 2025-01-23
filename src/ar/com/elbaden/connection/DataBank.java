package ar.com.elbaden.connection;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.modal.ErrorDialog;
import ar.com.elbaden.main.App;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBank {

    public static boolean testConnection(Window root) {
        boolean success = false;
        String url = App.settings.getProperties().getProperty(Settings.KEY_URL_CONNECT);
        String user = App.settings.getProperties().getProperty(Settings.KEY_USERNAME_DB);
        String pass = App.settings.getProperties().getProperty(Settings.KEY_PASSWORD_DB);
        try (Connection ignore = DriverManager.getConnection(url, user, pass)) {
            success = true;
        } catch (SQLException sqlException) {
            ErrorDialog.createAndShow(root, sqlException);
        }
        return success;
    }

}
