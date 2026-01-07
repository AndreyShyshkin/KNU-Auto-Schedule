package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.view.core.Refreshable;
import ua.kiev.univ.schedule.view.core.TabbedPane;

public class InputPane extends TabbedPane {

    public final DatePane datePane;
    public final ChairPane chairPane;
    public final SpecialityPane specialityPane;
    public final TeacherPane teacherPane;
    public final GroupPane groupPane;
    public final PlacementPane placementPane;
    public final SubjectPane subjectPane;
    public final LessonPane lessonPane;

    public InputPane(String key) {
        addTab("", datePane = new DatePane(this, key + ".date"));
        addTab("", chairPane = new ChairPane(this, key + ".chair"));
        addTab("", specialityPane = new SpecialityPane(this, key + ".speciality"));
        addTab("", teacherPane = new TeacherPane(this, key + ".teacher"));
        addTab("", groupPane = new GroupPane(this, key + ".group"));
        addTab("", placementPane = new PlacementPane(this, key + ".placement"));
        addTab("", subjectPane = new SubjectPane(this, key + ".subject"));
        addTab("", lessonPane = new LessonPane(this, key + ".lesson"));

        // Використовуємо лямбда-вираз для слухача зміни вкладки
        this.addChangeListener(e -> {
            // Оновлюємо дані тільки на активній вкладці для економії ресурсів
            ((Refreshable) getSelectedComponent()).refresh();
        });
    }

    @Override
    public void refresh() {
        datePane.refresh();
        chairPane.refresh();
        specialityPane.refresh();
        teacherPane.refresh();
        groupPane.refresh();
        placementPane.refresh();
        subjectPane.refresh();
        lessonPane.refresh();
    }

    @Override
    public void loadLanguage() {
        datePane.loadLanguage();
        chairPane.loadLanguage();
        specialityPane.loadLanguage();
        teacherPane.loadLanguage();
        groupPane.loadLanguage();
        placementPane.loadLanguage();
        subjectPane.loadLanguage();
        lessonPane.loadLanguage();
    }
}