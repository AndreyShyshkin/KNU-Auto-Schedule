package ua.kiev.univ.schedule.view.menu.options;

import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.Frame;

import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ItemEvent;

public class LanguageItem extends JRadioButtonMenuItem {

    public LanguageItem(final Frame frame, String language) {
        this.setText(language);
        this.setSelected(Language.DEFAULT.equals(language));


        this.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                frame.setLanguage(getText());
            }
        });
    }
}