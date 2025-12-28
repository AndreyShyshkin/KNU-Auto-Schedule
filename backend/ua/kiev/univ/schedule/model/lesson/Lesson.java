package ua.kiev.univ.schedule.model.lesson;

import ua.kiev.univ.schedule.model.placement.Earmark;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Lesson extends MemberedEntity {

    private Earmark earmark;
    private Integer count = 2;

    @Override
    public boolean isActive() {
        return super.isActive() && (earmark != null) && earmark.isActive();
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        earmark = readEntity(Earmark.class, is);
        count = is.readInt();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(earmark, Earmark.class, os);
        os.writeInt(count);
    }

    public Earmark getEarmark() {
        return earmark;
    }

    public void setEarmark(Earmark earmark) {
        this.earmark = earmark;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}