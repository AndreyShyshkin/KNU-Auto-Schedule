package ua.kiev.univ.schedule.model.lesson;

import ua.kiev.univ.schedule.model.core.EnablableEntity;
import ua.kiev.univ.schedule.model.subject.Subject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SubjectedEntity extends EnablableEntity {

    private Subject subject;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        subject = readEntity(Subject.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(subject, Subject.class, os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && (subject != null) && subject.isActive();
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}