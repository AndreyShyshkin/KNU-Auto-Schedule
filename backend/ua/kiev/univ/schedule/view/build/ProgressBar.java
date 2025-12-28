package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.Internationalized;

import javax.swing.JProgressBar;
import java.awt.Dimension;

public class ProgressBar extends JProgressBar implements Internationalized {

    private final String key;
    private String text;
    private Progress progress = Progress.STOP;

    public ProgressBar(String key) {
        this.key = key;
        setPreferredSize(new Dimension(300, 25));
        setMinimumSize(getPreferredSize());
        setStringPainted(true);
    }

    public void setString() {
        // getPercentComplete() повертає значення від 0.0 до 1.0
        setString(text + " : " + (int) (getPercentComplete() * 100) + "%");
    }

    @Override
    public void loadLanguage() {
        // Завантажуємо текст статусу (наприклад, "Будується", "Пауза")
        text = Language.getText(key + "." + progress);
        setString();
    }

    public void setProgress(Progress progress) {
        setValue(progress.value);
        if (progress != this.progress) {
            this.progress = progress;
            loadLanguage();
        } else {
            setString();
        }
    }
}