package ua.kiev.univ.schedule.model.placement;

import ua.kiev.univ.schedule.model.core.NamedEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Auditorium extends NamedEntity {

    private Earmark earmark;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        earmark = readEntity(Earmark.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(earmark, Earmark.class, os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && earmark.isActive();
    }

    public Earmark getEarmark() {
        return earmark;
    }

    public void setEarmark(Earmark earmark) {
        this.earmark = earmark;
    }
}