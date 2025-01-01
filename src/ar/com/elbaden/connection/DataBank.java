package ar.com.elbaden.connection;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

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
            JOptionPane.showMessageDialog(root, message, title, JOptionPane.ERROR_MESSAGE);
        }
        return driverExists;
    }

}
