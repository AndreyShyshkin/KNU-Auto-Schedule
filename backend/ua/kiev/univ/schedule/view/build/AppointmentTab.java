package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.util.HtmlUtils;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.Internationalized;
import ua.kiev.univ.schedule.view.core.Refreshable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class AppointmentTab<T extends Member> extends JScrollPane implements Refreshable, Internationalized {

    private final String key;
    private final AppointmentPane appointmentPane;
    private final Class<T> memberType;
    private final JLabel label = new JLabel();

    // Додано raw-type warning suppression, оскільки generic масиви/класи в Swing іноді конфліктують
    public AppointmentTab(Class<T> memberType, AppointmentPane appointmentPane, String key) {
        this.memberType = memberType;
        this.appointmentPane = appointmentPane;
        this.key = key;
        this.getViewport().add(label);
    }

    @Override
    public void loadLanguage() {
        int index = appointmentPane.indexOfComponent(this);
        if (index >= 0) {
            appointmentPane.setTitleAt(index, Language.getText(key));
        }
    }

    @Override
    public void refresh() {
        // Генеруємо HTML таблицю результатів
        label.setText(HtmlUtils.generateResultsHtml(memberType));
    }
}