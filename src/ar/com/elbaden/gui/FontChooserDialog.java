package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

public class FontChooserDialog extends JDialog implements PropertyChangeListener {

    private final JList<Font> admittedFontList;
    private final JList<Font> rejectedFontList;
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
        String tabOneTitle = messages.getString("dialog.fontChooser.admittedFontsTitle");
        String tabTwoTitle = messages.getString("dialog.fontChooser.rejectedFontsTitle");
        String previewPhrase = messages.getString("dialog.fontChooser.previewPhrase");

        // componentes
        JLabel infoLabel = new JLabel(loadingMessage);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        admittedFontList = new JList<>();
        admittedFontList.setCellRenderer(new ListFontRenderer(true));
        admittedFontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        rejectedFontList = new JList<>();
        rejectedFontList.setCellRenderer(new ListFontRenderer());
        admittedFontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTextArea fontPreviewArea = new JTextArea(previewPhrase);
        fontPreviewArea.setLineWrap(true);
        fontPreviewArea.setWrapStyleWord(true);

        JScrollPane admittedListScrollPane = new JScrollPane();
        admittedListScrollPane.getViewport().setView(admittedFontList);

        JScrollPane discardedListScrollPane = new JScrollPane();
        discardedListScrollPane.getViewport().setView(rejectedFontList);

        JScrollPane fontPreviewScrollPane = new JScrollPane();
        fontPreviewScrollPane.getViewport().setView(fontPreviewArea);

        JTabbedPane fontsTabbedPane = new JTabbedPane();

        JPanel loadingPanel = new JPanel(new BorderLayout());

        JPanel fontsPanel = new JPanel(null);

        // instalando componentes
        final String card1 = "loadingContent", card2 = "mainContent"; // nombres para las tarjetas
        loadingPanel.add(infoLabel);
        loadingPanel.add(progressBar, BorderLayout.SOUTH);
        getContentPane().add(loadingPanel, card1);

        fontsTabbedPane.addTab(tabOneTitle, admittedListScrollPane);
        fontsTabbedPane.addTab(tabTwoTitle, discardedListScrollPane);

        BoxLayout boxLayout = new BoxLayout(fontsPanel, BoxLayout.PAGE_AXIS);
        fontsPanel.setLayout(boxLayout);
        fontsPanel.add(fontsTabbedPane);
        fontsPanel.add(fontPreviewScrollPane);
        getContentPane().add(fontsPanel, card2);

        // eventos
        Function<JList<Font>, Font> useAsPreviewFont = list -> {
            if (list.getSelectedValue() != null) {
                return list.getSelectedValue();
            }
            return fontPreviewArea.getFont();
        };

        ListSelectionListener selectionListener = _ -> {
            int index = fontsTabbedPane.getSelectedIndex();
            if (index == -1) {
                return;
            }
            String selectedTitle = fontsTabbedPane.getTitleAt(index);
            if (selectedTitle.equals(tabOneTitle)) {
                fontPreviewArea.setFont(useAsPreviewFont.apply(admittedFontList));
            } else if (selectedTitle.equals(tabTwoTitle)) {
                fontPreviewArea.setFont(useAsPreviewFont.apply(rejectedFontList));
            }
        };
        PropertyChangeListener modelChanged = _ -> cardLayout.show(getContentPane(), card2);
        PropertyChangeListener fitPreview = _ -> {
            // calculo las dimensiones de las celdas de cada lista suponiendo que sean diferentes
            int listOneHeight = admittedFontList.getFixedCellHeight();
            int listTwoHeight = rejectedFontList.getFixedCellHeight();
            int listOneWidth = admittedFontList.getFixedCellWidth();
            int listTwoWidth = rejectedFontList.getFixedCellWidth();
            // ajusto el campo de texto a las dimensiones obtenidas
            int maxHeight = Math.max(listOneHeight, listTwoHeight);
            int maxWidth = Math.max(listOneWidth, listTwoWidth);
            // considero el espacio entre el texto y el componente
            Insets padding = fontPreviewArea.getInsets();
            if (padding != null) {
                maxHeight += padding.top + padding.bottom;
            }
            maxHeight *= 3; // multiplico la altura para poder mostrar más texto
            // creo la dimensión ajustada y la establezco al componente de vista previa
            Dimension dimension = new Dimension(maxWidth, maxHeight);
            fontPreviewScrollPane.setPreferredSize(dimension);
        };
        PropertyChangeListener fontApplied = _ -> selectedFont = fontPreviewArea.getFont();

        Consumer<JList<?>> fireSelection = list -> {
            int first = admittedFontList.getFirstVisibleIndex();
            int last = admittedFontList.getLastVisibleIndex();
            ListSelectionEvent event = new ListSelectionEvent(list, first, last, false);
            selectionListener.valueChanged(event);
        };

        fontsTabbedPane.addChangeListener(_ -> {
            int index = fontsTabbedPane.getSelectedIndex();
            String selectedTitle = fontsTabbedPane.getTitleAt(index);
            if (selectedTitle.equals(tabOneTitle)) {
                fireSelection.accept(admittedFontList);
            } else if (selectedTitle.equals(tabTwoTitle)) {
                fireSelection.accept(rejectedFontList);
            }
        });

        admittedFontList.addListSelectionListener(selectionListener);
        admittedFontList.addPropertyChangeListener("model", modelChanged);
        admittedFontList.addPropertyChangeListener("model", fitPreview);

        rejectedFontList.addListSelectionListener(selectionListener);
        rejectedFontList.addPropertyChangeListener("model", modelChanged);

        fontPreviewArea.addPropertyChangeListener("font", fontApplied);

        selectedFont = getFont(); // puede devolver null CUIDADO!
    }

    public static Font createAndShow(Window owner) {
        FontChooserDialog dialog = new FontChooserDialog(owner);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        FontWorker worker = new FontWorker(dialog.getAdmittedFontList(), dialog.getRejectedFontList());
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

    public JList<Font> getAdmittedFontList() {
        return admittedFontList;
    }

    public JList<Font> getRejectedFontList() {
        return rejectedFontList;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
