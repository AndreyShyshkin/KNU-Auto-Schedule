package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.view.core.Refreshable;
import ua.kiev.univ.schedule.view.core.TabbedPane;

public class AppointmentPane extends TabbedPane {

    // Вказуємо конкретні типи для дженериків, щоб уникнути попереджень
    public final AppointmentTab<Teacher> teacherPane;
    public final AppointmentTab<Group> groupPane;

    public AppointmentPane(String key) {
        teacherPane = new AppointmentTab<>(Teacher.class, this, key + ".teacher");
        groupPane = new AppointmentTab<>(Group.class, this, key + ".group");

        addTab("", teacherPane);
        addTab("", groupPane);

        this.addChangeListener(e -> {
            // Оновлюємо тільки активну вкладку
            ((Refreshable) AppointmentPane.this.getSelectedComponent()).refresh();
        });
    }

    @Override
    public void refresh() {
        teacherPane.refresh();
        groupPane.refresh();
    }

    @Override
    public void loadLanguage() {
        teacherPane.loadLanguage();
        groupPane.loadLanguage();
    }
}