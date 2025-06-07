package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ApplyingFonts extends CheckPoint<Integer> {

    private final Window window;
    private final Map<String, Object> defaults;
    private final Set<String> keys;

    public ApplyingFonts(Window window) {
        this.window = window;
        defaults = App.defaults();
        keys = defaults.keySet();
    }

    @Override
    public Integer call() throws Exception {
        if (Thread.interrupted()) {
            throw new InterruptedException(Thread.currentThread().getName());
        }
        try {
            Map<JComponent, Font> pendingList = new HashMap<>();
            findApplicableContent(window, pendingList);
            SwingUtilities.invokeLater(() -> {
                for (JComponent component : pendingList.keySet()) {
                    Font font = pendingList.get(component);
                    component.setFont(font);
                }
            });
            return pendingList.size();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    private void findApplicableContent(Component component, Map<JComponent, Font> pendingList) {
        if (component instanceof Container container) {
            for (Component c : container.getComponents()) {
                findApplicableContent(c, pendingList);
            }
        }
        if (component instanceof JComponent jComponent) {
            String key = findKey(jComponent);
            if (key != null) {
                pendingList.put(jComponent, (Font) defaults.get(key));
            }
        }
    }

    private String findKey(JComponent component) {
        String componentName = component.getClass().getSimpleName().substring(1);
        Optional<String> result = keys.stream().filter(k -> k.contains(componentName)).findFirst();
        return result.orElse(null);
    }

}
