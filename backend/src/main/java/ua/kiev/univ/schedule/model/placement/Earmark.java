package ua.kiev.univ.schedule.model.placement;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import ua.kiev.univ.schedule.model.core.NamedEntity;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.core.DataService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class Earmark extends NamedEntity {

    @OneToMany(mappedBy = "earmark", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auditorium> auditoriums = new ArrayList<>();

    public List<Auditorium> getAuditoriums() {
        return auditoriums;
    }

    public void setAuditoriums(List<Auditorium> auditoriums) {
        this.auditoriums = auditoriums;
    }

    private Integer size = 30;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        size = is.readInt();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        os.writeInt(size);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // Якщо видаляється призначення (наприклад, "Комп'ютерний клас"),
        // то видаляємо і всі аудиторії цього типу.
        Iterator<Auditorium> iterator = DataService.getEntities(Auditorium.class).iterator();
        while (iterator.hasNext()) {
            Auditorium auditorium = iterator.next();
            if (auditorium.getEarmark() == this) {
                iterator.remove();
                auditorium.onRemove();
            }
        }

        for (Lesson lesson : DataService.getEntities(Lesson.class)) {
            if (lesson.getEarmark() == this) {
                lesson.setEarmark(null);
            }
        }
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}