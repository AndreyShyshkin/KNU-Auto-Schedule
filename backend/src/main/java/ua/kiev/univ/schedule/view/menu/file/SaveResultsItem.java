package ua.kiev.univ.schedule.view.menu.file;

import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.MenuItem;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.IOException;

public class SaveResultsItem extends MenuItem {

    private final JFileChooser chooser = new JFileChooser("saves");

    public SaveResultsItem(final Frame frame, String key) {
        super(key);
        this.addActionListener(e -> {
            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    DataService.writeResults(chooser.getSelectedFile());
                    frame.refresh();
                } catch (IOException io) {
                    JOptionPane.showMessageDialog(frame, "Error saving results: " + io.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}