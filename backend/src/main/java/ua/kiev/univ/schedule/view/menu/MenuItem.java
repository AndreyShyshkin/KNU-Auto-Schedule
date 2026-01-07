package ua.kiev.univ.schedule.view.menu;

import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.Internationalized;

import javax.swing.JMenuItem;

public abstract class MenuItem extends JMenuItem implements Internationalized {

    private final String key;

    public MenuItem(String key) {
        this.key = key;
    }

    @Override
    public void loadLanguage() {
        this.setText(Language.getText(key));
    }
}