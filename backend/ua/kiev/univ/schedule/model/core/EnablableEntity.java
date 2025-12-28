package ua.kiev.univ.schedule.model.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class EnablableEntity extends ActivableEntity {

    private Boolean enable = true;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        enable = is.readBoolean();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        os.writeBoolean(enable);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}