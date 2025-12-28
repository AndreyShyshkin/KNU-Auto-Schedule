package ua.kiev.univ.schedule.view.menu.file;

import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.MenuItem;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.IOException;

public class SaveasItem extends MenuItem {

    public SaveasItem(final Frame frame, final JFileChooser chooser, String key) {
        super(key);
        this.addActionListener(e -> {
            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    DataService.write(chooser.getSelectedFile());
                    frame.refresh();
                } catch (IOException io) {
                    JOptionPane.showMessageDialog(frame, "Error saving file: " + io.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}