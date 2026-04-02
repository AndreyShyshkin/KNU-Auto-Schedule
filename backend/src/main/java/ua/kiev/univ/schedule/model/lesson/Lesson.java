package ua.kiev.univ.schedule.model.lesson;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Entity
public class Lesson extends MemberedEntity {

    @ManyToOne
    private Earmark earmark;

    @ManyToOne
    private Building building;

    @ManyToOne
    private Auditorium auditorium;

    private Integer count = 2;

    @Override
    public boolean isActive() {
        return super.isActive() && (earmark != null) && earmark.isActive();
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        earmark = readEntity(Earmark.class, is);
        building = readEntity(Building.class, is);
        auditorium = readEntity(Auditorium.class, is);
        count = is.readInt();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(earmark, Earmark.class, os);
        writeEntity(building, Building.class, os);
        writeEntity(auditorium, Auditorium.class, os);
        os.writeInt(count);
    }

    public Earmark getEarmark() {
        return earmark;
    }

    public void setEarmark(Earmark earmark) {
        this.earmark = earmark;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}