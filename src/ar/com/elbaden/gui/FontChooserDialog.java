package ar.com.elbaden.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FontChooserDialog extends JDialog implements PropertyChangeListener {

    private final JList<Font> availableFontList;
    private final JProgressBar progressBar;
    private Font selectedFont;

    private FontChooserDialog(Window owner) {
        super(owner);
        // ajustes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);

        // componentes
        availableFontList = new JList<>(new DefaultListModel<>());
        availableFontList.setCellRenderer(new ListFontRenderer());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(availableFontList);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // instalando componentes
        getContentPane().add(scrollPane);
        getContentPane().add(progressBar, BorderLayout.SOUTH);

        // eventos
        availableFontList.addListSelectionListener(_ -> selectedFont = availableFontList.getSelectedValue());
        availableFontList.addPropertyChangeListener("model", _ -> {
            pack();
            setLocationRelativeTo(getOwner());
        });

        selectedFont = getFont(); // puede devolver null CUIDADO!
    }

    public static Font createAndShow(Window owner) {
        FontChooserDialog dialog = new FontChooserDialog(owner);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        FontWorker worker = new FontWorker(dialog.getAvailableFontList());
        worker.addPropertyChangeListener(dialog);
        worker.execute();
        dialog.setVisible(true);
        return dialog.selectedFont;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            getProgressBar().setValue((Integer) evt.getNewValue());
        }
    }

    public JList<Font> getAvailableFontList() {
        return availableFontList;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
