package ua.kiev.univ.schedule.model.date;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import ua.kiev.univ.schedule.model.core.NamedEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Day extends NamedEntity {

    @ManyToMany
    @JoinTable(
        name = "day_time_slot",
        joinColumns = @JoinColumn(name = "day_id"),
        inverseJoinColumns = @JoinColumn(name = "time_slot_id")
    )
    private List<Time> times = new LinkedList<>();

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        times = readList(Time.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeList(times, Time.class, os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && isActive(times);
    }

    public List<Time> getTimes() {
        return times;
    }

    public void setTimes(List<Time> times) {
        this.times = times;
    }
}