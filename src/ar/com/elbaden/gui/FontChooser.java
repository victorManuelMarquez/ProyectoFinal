package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class FontChooser extends JDialog {

    private final FontFamilyList familyList;

    private FontChooser(Window owner) {
        super(owner);
        // ajustes
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // componentes
        familyList = new FontFamilyList();
        JScrollPane fontFamilyScrollPane = new JScrollPane();

        // instalando componentes
        fontFamilyScrollPane.getViewport().setView(familyList);
        getContentPane().add(fontFamilyScrollPane);
    }

    public static void createAndShow(Window owner) {
        FontChooser fontChooserDialog = new FontChooser(owner);
        LoadingDialog loadingDialog = new LoadingDialog(owner, null);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(owner);
        FontsLoader fontsLoader = new FontsLoader(fontChooserDialog.familyList);
        fontsLoader.addPropertyChangeListener(loadingDialog);
        loadingDialog.addWindowListener(fontsLoader);
        fontsLoader.execute();
        loadingDialog.setVisible(true);
        if (fontChooserDialog.familyList.getNames() == null) {
            return;
        }
        fontChooserDialog.pack();
        fontChooserDialog.setLocationRelativeTo(owner);
        fontChooserDialog.setVisible(true);
    }

    static class FontFamilyList extends JList<String> {

        private String[] names;

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

    }

    static class LoadingDialog extends JDialog implements PropertyChangeListener {

        private final Cursor defaultCursor;
        private final JProgressBar progressBar;

        public LoadingDialog(Window owner, String title) {
            super(owner, title);
            // ajustes
            setModal(true);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
                setUndecorated(true);
            }
            BorderLayout borderLayout = (BorderLayout) getLayout();
            borderLayout.setHgap(0); // limpio los márgenes
            borderLayout.setVgap(0); // limpio los márgenes

            // componentes
            defaultCursor = getCursor();
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);

            // instalando componentes
            getContentPane().add(progressBar, BorderLayout.SOUTH);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("state".equals(evt.getPropertyName())) {
                if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                } else if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    setCursor(defaultCursor);
                    setVisible(false);
                    dispose();
                }
            } else if ("progress".equals(evt.getPropertyName())) {
                Integer newValue = (Integer) evt.getNewValue();
                progressBar.setValue(newValue);
            } else if ("indeterminate".equals(evt.getPropertyName())) {
                boolean newValue = (boolean) evt.getNewValue();
                progressBar.setIndeterminate(newValue);
            }
        }

    }

    static class FontsLoader extends SwingWorker<String[], String> implements WindowListener {

        private final FontFamilyList fontFamilyList;
        private final DefaultListModel<String> listModel;

        public FontsLoader(FontFamilyList fontFamilyList) {
            this.fontFamilyList = fontFamilyList;
            listModel = new DefaultListModel<>();
        }

        @Override
        protected String[] doInBackground() throws Exception {
            firePropertyChange("indeterminate", false, true);
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (environment == null) {
                throw new ExecutionException(new IllegalStateException("null"));
            }
            String[] families;
            families = environment.getAvailableFontFamilyNames(Locale.getDefault());
            firePropertyChange("indeterminate", true, false);
            int value = 0;
            for (String family : families) {
                if (isCancelled()) {
                    throw new CancellationException();
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                value++;
                listModel.addElement(family);
                setProgress(value * 100 / families.length);
            }
            return families;
        }

        @Override
        protected void done() {
            try {
                fontFamilyList.setNames(get());
                fontFamilyList.setModel(listModel);
                String family = fontFamilyList.getFont().getFamily();
                fontFamilyList.setSelectedValue(family, true);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        @Override
        public void windowOpened(WindowEvent e) {}

        @Override
        public void windowClosing(WindowEvent e) {
            cancel(true);
        }

        @Override
        public void windowClosed(WindowEvent e) {}

        @Override
        public void windowIconified(WindowEvent e) {}

        @Override
        public void windowDeiconified(WindowEvent e) {}

        @Override
        public void windowActivated(WindowEvent e) {}

        @Override
        public void windowDeactivated(WindowEvent e) {}

    }

}
