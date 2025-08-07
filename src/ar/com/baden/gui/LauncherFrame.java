package ar.com.baden.gui;

import javax.swing.*;
import javax.swing.text.Caret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LauncherFrame extends JFrame {

    private LauncherFrame(String title) throws HeadlessException {
        super(title);
        /* variables */
        LaunchWorker worker;
        // componentes
        JTextArea infoArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(infoArea);
        JProgressBar progressBar = new JProgressBar();

        // instalando componentes
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(progressBar, BorderLayout.SOUTH);
        worker = new LaunchWorker(infoArea); // se requiere que el área de texto ya esté contenida en una ventana

        /* ajustes */
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
            if (JFrame.isDefaultLookAndFeelDecorated()) {
                getRootPane().setWindowDecorationStyle(JRootPane.NONE);
            }
        }
        // área de texto
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        infoArea.setEditable(false);
        infoArea.setFocusable(false);
        Caret caret = infoArea.getCaret();
        caret.setVisible(false);
        // barra de progreso
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);

        // eventos
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof Integer percentage) {
                    if (progressBar.isIndeterminate()) {
                        progressBar.setIndeterminate(false);
                    }
                    progressBar.setValue(percentage);
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                worker.execute();
            }
        });
    }

    protected void calculateSize() {
        // tamaño del monitor principal
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.ceilDiv(screenSize.width, 3);
        int height = Math.ceilDiv(screenSize.height, 3);
        setSize(new Dimension(width, height));
    }

    public static void createAndShow() {
        try {
            String title = LauncherFrame.class.getSimpleName();
            LauncherFrame frame = new LauncherFrame(title);
            frame.calculateSize();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

}
