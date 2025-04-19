package ar.com.elbaden.background.task;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CheckApplyingRoboto implements Callable<String> {

    private final ResourceBundle messages;
    private final JComponent component;

    public CheckApplyingRoboto(ResourceBundle messages, JComponent component) {
        this.messages = messages;
        this.component = component;
    }

    public String call() throws Exception {
        try {
            String fontName = "Roboto";

            if (Thread.interrupted()) {
                String reason = messages.getString("interrupted");
                String templateMessage = messages.getString("checkpoint.interrupted.loadingFont.name.reason");
                throw new InterruptedException(MessageFormat.format(templateMessage, fontName, reason));
            }

            UIDefaults defaults = UIManager.getDefaults();
            Predicate<Map.Entry<Object, Object>> fontsOnly;
            fontsOnly = entry -> entry.getValue() instanceof Font;
            Stream<Map.Entry<Object, Object>> fonts = defaults.entrySet().stream().filter(fontsOnly);

            Font robotoFont = new Font(fontName, Font.PLAIN, 18);

            Map<String, Font> fontMap = new HashMap<>();

            fonts.forEach(entry -> {
                Font defaultFont = UIManager.getFont(entry.getKey());
                Font newRobotoFont = robotoFont.deriveFont(defaultFont.getStyle(), (float) defaultFont.getSize());
                fontMap.put(entry.getKey().toString(), newRobotoFont);
            });

            fontMap.forEach(UIManager::put);
            fontMap.forEach((k,v) -> System.out.println(k + ":" + v));

            String templateMessage = messages.getString("checkpoint.applyingFont.name");

            if (robotoFont.getFontName().matches("(?i).*" + fontName + ".*")) {
                Window window = SwingUtilities.getWindowAncestor(component);
                SwingUtilities.invokeAndWait(() -> {
                    window.revalidate();
                    window.repaint();
                });
            } else {
                templateMessage = messages.getString("checkpoint.error.applyingFont.defaultReturned");
                throw new RuntimeException(MessageFormat.format(templateMessage, fontName));
            }

            return MessageFormat.format(templateMessage, fontName);
        } catch (RuntimeException e) {
            throw new ExecutionException(e);
        }
    }

}
