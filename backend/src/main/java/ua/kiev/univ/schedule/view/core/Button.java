package ua.kiev.univ.schedule.view.core;

import ua.kiev.univ.schedule.util.Language;

import javax.swing.JButton;
import java.awt.Dimension;

public abstract class Button extends JButton implements Internationalized, Refreshable {

    private final String key;

    public Button(String key) {
        this.key = key;
        Dimension size = new Dimension(100, 20);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
    }

    @Override
    public void loadLanguage() {
        this.setText(Language.getText(key));
    }
}