package ar.com.elbaden.gui;


import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class LoadingFontsDialog extends JDialog implements PropertyChangeListener {

    private final JProgressBar progressBar;
    private final Cursor defaultCursor;
    private List<Font> results;

    public LoadingFontsDialog(Window owner, String title) {
        super(owner, title);
        // ajustes
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        defaultCursor = getCursor();
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }

        // componentes
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(progressBar);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.STARTED == evt.getNewValue()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else if ("progress".equals(evt.getPropertyName())) {
            getProgressBar().setValue((Integer) evt.getNewValue());
        } else if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
            setCursor(defaultCursor);
            setVisible(false);
            dispose();
        }
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public List<Font> getResults() {
        return results;
    }

    protected void setResults(List<Font> results) {
        this.results = results;
    }

}
