package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;
import ar.com.elbaden.task.AppChecker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public final class LoadingScreen extends JFrame {

    private LoadingScreen() throws HeadlessException {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations())
            setUndecorated(true);
        // componentes
        Border emptyBorder = BorderFactory.createEmptyBorder(4, 8, 4, 8);

        JTextArea infoArea = new JTextArea();
        infoArea.setBackground(new Color(255, 255, 255, 0));
        infoArea.setOpaque(false);
        infoArea.setEditable(false);
        infoArea.setFocusable(false);
        infoArea.setDisabledTextColor(UIManager.getColor("TextArea.foreground"));

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(emptyBorder);
        scrollPane.setPreferredSize(new Dimension(360, 240));

        JProgressBar infoProgress = new JProgressBar();
        infoProgress.setStringPainted(true);

        // instalando los componentes en el frame
        getContentPane().add(scrollPane);
        getContentPane().add(infoProgress, BorderLayout.SOUTH);

        // eventos
        WindowEvents windowEvents = new WindowEvents(infoArea, infoProgress);

        addWindowListener(windowEvents);
    }

    public static void createAndShow() {
        LoadingScreen window = new LoadingScreen();
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    static class WindowEvents extends WindowAdapter implements PropertyChangeListener, ActionListener {

        private final Window root;
        private final JProgressBar publishProgress;
        private final AppChecker checker;
        private final Timer countdown;
        private final int seconds = 15;
        private int counter = seconds;

        private static final Logger GLOBAL_LOGGER = Logger.getGlobal();
        private final ResourceBundle messages;

        private WindowEvents(JTextArea publisher, JProgressBar publishProgress) {
            this.publishProgress = publishProgress;
            root = SwingUtilities.windowForComponent(publisher);
            checker = new AppChecker(publisher);
            checker.addPropertyChangeListener(this);
            countdown = new Timer(1000, this);
            messages = ResourceBundle.getBundle(App.LOCALES_DIR);
        }

        @Override
        public void windowOpened(WindowEvent e) {
            GLOBAL_LOGGER.fine(messages.getString("fine.show_loading_screen"));
            getChecker().execute();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (!getChecker().isDone()) {
                getChecker().cancel(true);
            }
            if (getCountdown().isRunning()) {
                getCountdown().stop();
            }
            GLOBAL_LOGGER.info(messages.getString("info.user_closing_loading_screen"));
            getRoot().dispose();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getPublishProgress().setValue(counter * 100 / seconds);
            if (counter < 0) {
                GLOBAL_LOGGER.fine(messages.getString("fine.close_loading_screen"));
                getCountdown().stop();
                getRoot().dispose();
            } else {
                counter--;
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                getPublishProgress().setValue(getChecker().getProgress());
            } else if ("countdown".equals(evt.getPropertyName())) {
                getCountdown().start();
            } else if ("indeterminate".equals(evt.getPropertyName())) {
                getPublishProgress().setIndeterminate((Boolean) evt.getNewValue());
            }
        }

        public Window getRoot() {
            return root;
        }

        public JProgressBar getPublishProgress() {
            return publishProgress;
        }

        public AppChecker getChecker() {
            return checker;
        }

        public Timer getCountdown() {
            return countdown;
        }

    }

}
