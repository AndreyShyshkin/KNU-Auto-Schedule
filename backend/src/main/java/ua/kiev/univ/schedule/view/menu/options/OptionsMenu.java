package ua.kiev.univ.schedule.view.menu.options;

import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.Menu;

public class OptionsMenu extends Menu {

    public OptionsMenu(Frame frame, String key) {
        super(key);
        this.add(new LanguageMenu(frame, key + ".language"));
    }
}