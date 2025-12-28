package ua.kiev.univ.schedule.model.department;

import ua.kiev.univ.schedule.model.core.NamedEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DescriptionedEntity extends NamedEntity {

    protected String description = "";

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        description = is.readUTF();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        os.writeUTF(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}