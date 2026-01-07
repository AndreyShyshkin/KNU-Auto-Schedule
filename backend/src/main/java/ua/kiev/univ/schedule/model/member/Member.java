package ua.kiev.univ.schedule.model.member;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import ua.kiev.univ.schedule.model.core.NamedEntity;
import ua.kiev.univ.schedule.model.department.Department;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@MappedSuperclass
public abstract class Member<E extends Department> extends NamedEntity {

    @Transient
    private E department;

    protected abstract Class<E> departmentClass();

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        department = readEntity(departmentClass(), is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(department, departmentClass(), os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && department.isActive();
    }

    public E getDepartment() {
        return department;
    }

    public void setDepartment(E department) {
        this.department = department;
    }
}