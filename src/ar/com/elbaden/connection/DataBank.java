package ar.com.elbaden.connection;

import ar.com.elbaden.gui.modal.FixedOptionPane;

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
            String localFatalError = "¡Error fatal!";
            String localMessage = "El driver no existe o no puede ser utilizado.";
            String message = localFatalError + System.lineSeparator() + localMessage;
            String title = e.getClass().getSimpleName();
            FixedOptionPane.showMessageDialog(root, message, title, JOptionPane.ERROR_MESSAGE);
        }
        return driverExists;
    }

    public static boolean canConnect(JFrame root) {
        boolean connected = false;
        try (Connection ignore = DriverManager.getConnection("mysql:jdbc://localhost:3306", "root", "")) {
            connected = true;
        } catch (SQLException sqlException) {
            StringBuilder message = buildMessageFor(sqlException);
            String title = sqlException.getClass().getSimpleName();
            FixedOptionPane.showMessageDialog(root, message, title, JOptionPane.ERROR_MESSAGE);
        }
        return connected;
    }

    private static StringBuilder buildMessageFor(SQLException sqlException) {
        String formattedLocalError = "Se ha producido un error: %s";
        String formattedLocalSQLError = "SQL ha dicho: %s";
        String formattedLocalErrorCode = "Código de error: %s";
        StringBuilder message = new StringBuilder();
        message.append(String.format(formattedLocalError, sqlException.getMessage()));
        message.append(System.lineSeparator());
        message.append(String.format(formattedLocalSQLError, sqlException.getSQLState()));
        message.append(System.lineSeparator());
        message.append(String.format(formattedLocalErrorCode, sqlException.getErrorCode()));
        return message;
    }

}
