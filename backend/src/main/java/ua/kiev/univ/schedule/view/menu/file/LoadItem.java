package ua.kiev.univ.schedule.view.menu.file;

import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.MenuItem;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LoadItem extends MenuItem {

    public LoadItem(final Frame frame, final JFileChooser chooser, String key) {
        super(key);
        this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        this.addActionListener(e -> {
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    DataService.read(chooser.getSelectedFile());
                    frame.refresh();
                } catch (IOException io) {
                    JOptionPane.showMessageDialog(frame, "Error loading file: " + io.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}