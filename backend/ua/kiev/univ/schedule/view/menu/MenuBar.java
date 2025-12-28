package ua.kiev.univ.schedule.view.menu;

import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.core.Internationalized;
import ua.kiev.univ.schedule.view.menu.file.FileMenu;
import ua.kiev.univ.schedule.view.menu.help.HelpMenu;
import ua.kiev.univ.schedule.view.menu.options.OptionsMenu;

import javax.swing.JMenuBar;
import javax.swing.MenuElement;

public class MenuBar extends JMenuBar implements Internationalized {

    public MenuBar(Frame frame, String key) {
        this.add(new FileMenu(frame, key + ".file"));
        this.add(new OptionsMenu(frame, key + ".options"));
        this.add(new HelpMenu(key + ".help"));
    }

    @Override
    public void loadLanguage() {
        for (MenuElement element : this.getSubElements()) {
            if (element instanceof Internationalized) {
                ((Internationalized) element).loadLanguage();
            }
        }
    }
}