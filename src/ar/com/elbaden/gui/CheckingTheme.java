package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CheckingTheme extends CheckPoint<String> {

    private final String themeValid;
    private final String themeInvalid;

    public CheckingTheme() {
        themeValid = App.MESSAGES.getString("f.themeValid");
        themeInvalid = App.MESSAGES.getString("f.themeInvalid");
    }

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            boolean isValid;
            String className = (String) App.defaults().get(Settings.THEME_KEY);
            List<UIManager.LookAndFeelInfo> installedThemes = List.of(UIManager.getInstalledLookAndFeels());
            isValid = installedThemes.stream().anyMatch(info -> info.getClassName().equals(className));
            if (isValid) {
                return MessageFormat.format(themeValid, className);
            } else {
                throw new RuntimeException(MessageFormat.format(themeInvalid, className));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
