package ua.kiev.univ.schedule.model.department;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Department extends DescriptionedEntity {

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

    @Override
    public boolean isActive() {
        return super.isActive() && faculty.isActive();
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
}