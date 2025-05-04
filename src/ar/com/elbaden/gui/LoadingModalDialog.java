package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoadingModalDialog extends JDialog implements PropertyChangeListener {

    private final Cursor defaultCursor;
    private final JProgressBar progressBar;

    public LoadingModalDialog(Window owner, String title) {
        super(owner, title);
        // ajustes
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        defaultCursor = getCursor();
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(progressBar, BorderLayout.SOUTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.STARTED == evt.getNewValue()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
            setCursor(defaultCursor);
            setVisible(false);
            dispose();
        } else if ("progress".equals(evt.getPropertyName())) {
            Integer newValue = (Integer) evt.getNewValue();
            progressBar.setValue(newValue);
        }
    }

}
