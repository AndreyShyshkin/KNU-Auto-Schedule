package ua.kiev.univ.schedule.view.menu.options;

import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.Menu;

import javax.swing.ButtonGroup;

public class LanguageMenu extends Menu {

    public LanguageMenu(Frame frame, String key) {
        super(key);
        ButtonGroup group = new ButtonGroup();
        for (String language : Language.getLanguages()) {
            LanguageItem item = new LanguageItem(frame, language);
            group.add(item);
            this.add(item);
        }
    }
}