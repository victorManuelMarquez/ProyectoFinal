package ar.com.elbaden.connection;

import ar.com.elbaden.gui.modal.PublishError;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBank {

    public static boolean isDriverPresent(JFrame root) {
        boolean driverExists = false;
        try {
            String driverClassPath = "com.mysql.cj.jdbc.Driver";
            Class<?> driverClass = Class.forName(driverClassPath);
            Object ignore = driverClass.getDeclaredConstructor().newInstance();
            driverExists = true;
        } catch (ClassNotFoundException | InvocationTargetException |
                 InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            PublishError.createAndShow(root, e);
        }
        return driverExists;
    }

    public static boolean canConnect(JFrame root) {
        boolean connected = false;
        try (Connection ignore = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "")) {
            connected = true;
        } catch (SQLException sqlException) {
            PublishError.createAndShow(root, sqlException);
        }
        return connected;
    }

}
