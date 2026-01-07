package ua.kiev.univ.schedule.view.menu.file;

import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.MenuItem;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ExitItem extends MenuItem {

    public ExitItem(final Frame frame, String key) {
        super(key);
        this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        this.addActionListener(e -> frame.close());
    }
}