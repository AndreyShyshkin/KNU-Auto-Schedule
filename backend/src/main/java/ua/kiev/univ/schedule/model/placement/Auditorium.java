package ua.kiev.univ.schedule.model.placement;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import ua.kiev.univ.schedule.model.core.NamedEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Entity
public class Auditorium extends NamedEntity {

    @ManyToOne
    private Earmark earmark;

    @ManyToOne
    private Building building;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        earmark = readEntity(Earmark.class, is);
        building = readEntity(Building.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(earmark, Earmark.class, os);
        writeEntity(building, Building.class, os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && earmark.isActive() && (building == null || building.isActive());
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
}