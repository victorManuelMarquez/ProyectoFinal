package ar.com.elbaden.main;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ThreadDemo implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new ThreadDemo());
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton button = new JButton("Lanzar otra ventana.");
        frame.add(button, BorderLayout.NORTH);
        JTextArea textArea = new JTextArea();
        textArea.setToolTipText("Escribe algo");
        frame.add(new JScrollPane(textArea));
        JLabel label = new JLabel("Texto de ejemplo en un JLabel");
        frame.add(label, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Worker worker = new Worker(label);
        worker.execute();
        button.addActionListener(_ -> {
            JFrame frame1 = new JFrame("Otra ventana");
            frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame1.add(new JLabel("Otro texto..."));
            frame1.pack();
            frame1.setLocationRelativeTo(null);
            frame1.setVisible(true);
        });
    }

    static class Worker extends SwingWorker<Void, String> {

        private final JComponent component;

        public Worker(JComponent component) {
            this.component = component;
        }

        @Override
        protected Void doInBackground() {
            try {
                Thread[] threads = new Thread[] {
                        new LoadFont("Noto Sans"),
                        new ApplyingFont(component)
                };
                for (Thread thread : threads) {
                    thread.setDaemon(true);
                    thread.start();
                    thread.join();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }

    }

    static class LoadFont extends Thread {

        private final String fontName;

        public LoadFont(String fontName) {
            this.fontName = fontName;
        }

        @Override
        public void run() {
            UIDefaults defaults = UIManager.getDefaults();
            Predicate<Map.Entry<Object, Object>> fontsOnly;
            fontsOnly = entry -> entry.getKey() instanceof String k && k.endsWith(".font");
            Stream<Map.Entry<Object, Object>> fonts = defaults.entrySet().stream().filter(fontsOnly);

            fonts.forEach(entry -> {
                Font defaultFont = UIManager.getFont(entry.getKey());
                Font newRobotoFont = new Font(fontName, defaultFont.getStyle(), defaultFont.getSize());
                UIManager.put(entry.getKey(), newRobotoFont);
            });
        }

    }

    static class ApplyingFont extends Thread {

        private final JComponent origin;

        public ApplyingFont(JComponent origin) {
            this.origin = origin;
        }

        @Override
        public void run() {
            UIDefaults defaults = UIManager.getDefaults();
            Stream<Map.Entry<Object, Object>> onlyFonts = defaults.entrySet().stream().filter(e -> e.getKey() instanceof String k && k.endsWith(".font"));
            List<String> fontKeys = onlyFonts.map(e -> e.getKey().toString()).toList();
            Stream<Map.Entry<Object, Object>> accelerators = defaults.entrySet().stream().filter(e -> e.getKey() instanceof String k && k.endsWith(".acceleratorFont"));
            List<String> accesibleFontKeys = accelerators.map(e -> e.getKey().toString()).toList();
            SwingUtilities.invokeLater(() -> {
                Window window = SwingUtilities.getWindowAncestor(origin);
                if (window != null) {
                    updateActualComponent(window, fontKeys, accesibleFontKeys);
                    window.revalidate();
                    window.repaint();
                    window.pack();
                }
            });
        }

        public void updateActualComponent(Component component, List<String> fonts, List<String> accessibleFonts) {
            if (component instanceof Container container) {
                for (Component c : container.getComponents()) {
                    updateActualComponent(c, fonts, accessibleFonts);
                }
            }
            if (component instanceof JComponent jComponent) {
                String componentId = jComponent.getUIClassID().replace("UI", "");
                Optional<String> fontProperty = fonts.stream().filter(v -> v.startsWith(componentId)).findFirst();
                Optional<String> acceleratorProperty = accessibleFonts.stream().filter(v -> v.startsWith(componentId)).findFirst();
                fontProperty.ifPresent(v -> printUpdate(jComponent, v));
                acceleratorProperty.ifPresent(v -> printUpdate(jComponent, v));
            }
        }

        private void printUpdate(JComponent jComponent, String value) {
            Font newFont = UIManager.getFont(value);
            Font originalFont = jComponent.getFont();
            jComponent.setFont(newFont);
            System.out.printf("Se reemplazó %s=%s por %s.\n", value, originalFont.getFontName(), newFont.getFontName());
        }

    }

}