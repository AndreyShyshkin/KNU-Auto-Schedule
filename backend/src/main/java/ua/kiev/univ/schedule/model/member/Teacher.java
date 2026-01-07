package ua.kiev.univ.schedule.model.member;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.core.DataService;

@Entity
public class Teacher extends Restrictor<Chair> {

    @ManyToOne
    private Chair department;

    @Override
    public Chair getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Chair department) {
        this.department = department;
    }

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