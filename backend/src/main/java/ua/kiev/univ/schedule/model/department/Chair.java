package ua.kiev.univ.schedule.model.department;

import jakarta.persistence.Entity;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.Iterator;

@Entity
public class Chair extends Department {

    @Override
    public void onRemove() {
        super.onRemove();
        Iterator<Teacher> iterator = DataService.getEntities(Teacher.class).iterator();
        while (iterator.hasNext()) {
            Teacher teacher = iterator.next();
            if (teacher.getDepartment() == this) {
                iterator.remove();
                teacher.onRemove();
            }
        }
    }
}