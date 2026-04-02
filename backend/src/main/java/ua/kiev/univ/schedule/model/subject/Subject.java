package ua.kiev.univ.schedule.model.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import ua.kiev.univ.schedule.model.core.NamedEntity;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.core.DataService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Entity
public class Subject extends NamedEntity {

    @ManyToOne
    private Faculty faculty;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        faculty = readEntity(Faculty.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(faculty, Faculty.class, os);
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    @Override
    public void onRemove() {
        super.onRemove();

        for (Lesson lesson : DataService.getEntities(Lesson.class)) {
            if (lesson.getSubject() == this) {
                lesson.setSubject(null);
            }
        }
    }
}