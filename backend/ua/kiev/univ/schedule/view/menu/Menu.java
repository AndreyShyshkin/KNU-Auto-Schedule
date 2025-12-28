package ua.kiev.univ.schedule.view.menu;

import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.Internationalized;

import javax.swing.JMenu;
import javax.swing.MenuElement;

public abstract class Menu extends JMenu implements Internationalized {

    private final String key;

    public Menu(String key) {
        this.key = key;
    }

    @Override
    public void loadLanguage() {
        this.setText(Language.getText(key));
        // Оновлюємо мову для всіх підпунктів
        for (MenuElement element : this.getPopupMenu().getSubElements()) {
            if (element instanceof Internationalized) {
                ((Internationalized) element).loadLanguage();
            }
        }
    }
}