package ua.kiev.univ.schedule.model.member;

import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.core.DataService;

public class Teacher extends Restrictor<Chair> {

    @Override
    protected Class<Chair> departmentClass() {
        return Chair.class;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // Видаляємо викладача з усіх занять
        for (Lesson lesson : DataService.getEntities(Lesson.class)) {
            lesson.getTeachers().remove(this);
        }
    }
}