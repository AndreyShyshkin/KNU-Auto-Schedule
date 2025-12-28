package ua.kiev.univ.schedule.view.menu.file;

import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.MenuItem;

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class NewItem extends MenuItem {

    public NewItem(final Frame frame, final JFileChooser chooser, String key) {
        super(key);
        this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        this.addActionListener(e -> {
            chooser.setSelectedFile(null);
            DataService.clear();
            frame.refresh();
        });
    }
}