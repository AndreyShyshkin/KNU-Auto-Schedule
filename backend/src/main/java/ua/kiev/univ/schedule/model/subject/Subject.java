package ua.kiev.univ.schedule.model.subject;

import jakarta.persistence.Entity;
import ua.kiev.univ.schedule.model.core.NamedEntity;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.core.DataService;

@Entity
public class Subject extends NamedEntity {

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