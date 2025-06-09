package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ApplyingFonts extends CheckPoint<Integer> {

    private final Window window;
    private final Map<String, Object> defaults;

    public ApplyingFonts(Window window) {
        this.window = window;
        defaults = App.defaults();
    }

    @Override
    public Integer call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Map<Component, Font> pendingList = new HashMap<>();
            findApplicableContent(window, pendingList);
            SwingUtilities.invokeLater(() -> {
                for (Component component : pendingList.keySet()) {
                    Font font = pendingList.get(component);
                    component.setFont(font);
                }
            });
            return pendingList.size();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    private void findApplicableContent(Component component, Map<Component, Font> pendingList) {
        if (component instanceof Container container) {
            for (Component c : container.getComponents()) {
                findApplicableContent(c, pendingList);
            }
        }
        String key = Settings.findKey(component);
        if (key != null) {
            pendingList.put(component, (Font) defaults.get(key));
        }
    }

}
