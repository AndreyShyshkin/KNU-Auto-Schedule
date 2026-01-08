package ua.kiev.univ.schedule.model.department;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class Chair extends Department {

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Teacher> teachers = new ArrayList<>();

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

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