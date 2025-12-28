package ua.kiev.univ.schedule.view.core;

import ua.kiev.univ.schedule.util.Language;

import javax.swing.JLabel;

public class Label extends JLabel implements Internationalized {

    private final String key;

    public Label(String key) {
        this.key = key;
    }

    protected String getLabelText() {
        return Language.getText(key);
    }

    @Override
    public void loadLanguage() {
        setText(getLabelText());
    }
}