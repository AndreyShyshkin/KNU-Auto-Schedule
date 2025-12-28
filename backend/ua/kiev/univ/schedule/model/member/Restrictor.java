package ua.kiev.univ.schedule.model.member;

import ua.kiev.univ.schedule.model.department.Department;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Restrictor<E extends Department> extends Member<E> {

    private Restriction restriction = new Restriction();

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        restriction.read(is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        restriction.write(os);
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }
}