package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RetrievingFonts extends CheckPoint<String> {

    @Override
    public String call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Map<String, Font> fontMap = App.settings.getFontsMap();
            App.fontMap = fontMap;
            return Integer.toString(fontMap.size());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
