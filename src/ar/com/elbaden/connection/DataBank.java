package ar.com.elbaden.connection;

import ar.com.elbaden.data.Settings;
import ar.com.elbaden.gui.modal.ErrorDialog;
import ar.com.elbaden.main.App;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public final class DataBank {

    private static final Logger GLOBAL_LOGGER = Logger.getGlobal();

    public static final String DATABASE_NAME = "el_baden";

    static final String USERS_TABLE_NAME = "USUARIOS";

    public static boolean testConnection(Window root) {
        boolean success = false;
        String url = App.settings.getProperties().getProperty(Settings.KEY_URL_CONNECT);
        String user = App.settings.getProperties().getProperty(Settings.KEY_USERNAME_DB);
        String pass = App.settings.getProperties().getProperty(Settings.KEY_PASSWORD_DB);
        try (Connection ignore = DriverManager.getConnection(url, user, pass)) {
            success = true;
        } catch (SQLException sqlException) {
            GLOBAL_LOGGER.severe(sqlException.getLocalizedMessage());
            ErrorDialog.createAndShow(root, sqlException);
        }
        return success;
    }

    public static int executeDML(StringQuery query, Window origin) {
        String url = App.settings.getProperties().getProperty(Settings.KEY_URL_CONNECT);
        String user = App.settings.getProperties().getProperty(Settings.KEY_USERNAME_DB);
        String pass = App.settings.getProperties().getProperty(Settings.KEY_PASSWORD_DB);
        try (
                Connection connection = DriverManager.getConnection(url, user, pass);
                Statement statement = connection.createStatement()
        ) {
            return statement.executeUpdate(query.sql());
        } catch (SQLException e) {
            ErrorDialog.createAndShow(origin, e);
            return -1;
        }
    }

}
