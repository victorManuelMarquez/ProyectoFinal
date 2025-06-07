package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CheckingTheme extends CheckPoint<Boolean> {

    @Override
    public Boolean call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            boolean isValid;
            String className = (String) App.defaults().get(Settings.THEME_KEY);
            List<UIManager.LookAndFeelInfo> installedThemes = List.of(UIManager.getInstalledLookAndFeels());
            isValid = installedThemes.stream().anyMatch(info -> info.getClassName().equals(className));
            return isValid;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
