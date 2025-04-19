package ar.com.elbaden.background.task;

import ar.com.elbaden.utils.FontLoader;

import java.awt.*;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CheckRobotoFont extends FontLoader implements Callable<String> {

    private final ResourceBundle messages;

    public CheckRobotoFont(ResourceBundle messages) {
        this.messages = messages;
    }

    @Override
    public String call() throws Exception {
        try {
            String fontName = "Roboto";
            if (Thread.interrupted()) {
                String reason = messages.getString("interrupted");
                String templateMessage = messages.getString("checkpoint.interrupted.loadingFont.name.reason");
                throw new InterruptedException(MessageFormat.format(templateMessage, fontName, reason));
            }
            String templateMessage = messages.getString("checkpoint.loadingFont");
            try {
                List<InputStream> sourceList = getStreamsOf("Roboto");
                Font[] robotoFamily = createFontFamilyFrom(sourceList, 12);
                registerFontsOf(robotoFamily);
                return MessageFormat.format(templateMessage, fontName);
            } catch (Exception e) {
                String reason = e.getLocalizedMessage();
                templateMessage = messages.getString("checkpoint.error.loadingFont.name.reason");
                throw new RuntimeException(MessageFormat.format(templateMessage, fontName, reason));
            }
        } catch (RuntimeException e) {
            throw new ExecutionException(e);
        }
    }

}
