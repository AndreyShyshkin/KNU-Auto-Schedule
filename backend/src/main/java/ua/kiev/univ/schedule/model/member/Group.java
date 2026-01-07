package ua.kiev.univ.schedule.model.member;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.core.DataService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Entity
@Table(name = "student_group")
public class Group extends Restrictor<Speciality> {

    @ManyToOne
    private Speciality department;

    private Year year = Year.FIRST;
    private Integer size = 20;

    @Override
    public Speciality getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Speciality department) {
        this.department = department;
    }

    @Override
    protected Class<Speciality> departmentClass() {
        return Speciality.class;
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        year = Year.values()[is.readInt()];
        size = is.readInt();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        os.writeInt(year.ordinal());
        os.writeInt(size);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // Видаляємо групу з усіх занять
        for (Lesson lesson : DataService.getEntities(Lesson.class)) {
            lesson.getGroups().remove(this);
        }
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}