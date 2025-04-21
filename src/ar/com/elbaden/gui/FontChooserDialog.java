package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

public class FontChooserDialog extends JDialog implements PropertyChangeListener {

    private final JList<Font> availableFontList;
    private final JProgressBar progressBar;
    private Font selectedFont;

    private FontChooserDialog(Window owner) {
        super(owner);
        // ajustes
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            setUndecorated(true);
        }
        CardLayout cardLayout = new CardLayout();
        setLayout(cardLayout);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);

        // localización
        ResourceBundle messages = ResourceBundle.getBundle(App.MESSAGES);
        setTitle(messages.getString("dialog.fontChooser.title"));
        String loadingMessage = messages.getString("dialog.fontChooser.loadingFontsPleaseWait");

        // componentes
        JLabel infoLabel = new JLabel(loadingMessage);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        availableFontList = new JList<>();
        availableFontList.setCellRenderer(new ListFontRenderer());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(availableFontList);

        JPanel loadingPanel = new JPanel(new BorderLayout());

        // instalando componentes
        final String card1 = "loadingContent", card2 = "mainContent"; // nombres para las tarjetas
        loadingPanel.add(infoLabel);
        loadingPanel.add(progressBar, BorderLayout.SOUTH);
        getContentPane().add(loadingPanel, card1);
        getContentPane().add(scrollPane, card2); // por ahora el único contenido

        // eventos
        ListSelectionListener fontSelection = _ -> selectedFont = availableFontList.getSelectedValue();
        PropertyChangeListener modelChanged = _ -> {
            if (isUndecorated()) {
                setUndecorated(false);
            }
            cardLayout.show(getContentPane(), card2);
        };
        availableFontList.addListSelectionListener(fontSelection);
        availableFontList.addPropertyChangeListener("model", modelChanged);

        selectedFont = getFont(); // puede devolver null CUIDADO!
    }

    public static Font createAndShow(Window owner) {
        FontChooserDialog dialog = new FontChooserDialog(owner);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        FontWorker worker = new FontWorker(dialog.getAvailableFontList());
        worker.addPropertyChangeListener(dialog);
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        worker.execute();
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!worker.isDone()) {
                    worker.cancel(true);
                }
            }
        });
        dialog.setVisible(true);
        return dialog.selectedFont;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            getProgressBar().setValue((Integer) evt.getNewValue());
        } else if ("state".equals(evt.getPropertyName()) && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public JList<Font> getAvailableFontList() {
        return availableFontList;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
