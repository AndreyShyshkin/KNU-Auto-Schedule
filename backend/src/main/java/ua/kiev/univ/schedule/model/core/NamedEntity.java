package ua.kiev.univ.schedule.model.core;

import jakarta.persistence.MappedSuperclass;
import ua.kiev.univ.schedule.util.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@MappedSuperclass
public abstract class NamedEntity extends EnablableEntity {

    private String name = "";

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        name = is.readUTF();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        os.writeUTF(name);
    }

    @Override
    public boolean isActive() {
        // StringUtils буде червоним до моменту створення цього класу
        return super.isActive() && !StringUtils.isBlank(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}