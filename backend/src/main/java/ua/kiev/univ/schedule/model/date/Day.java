package ua.kiev.univ.schedule.model.date;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import ua.kiev.univ.schedule.model.core.NamedEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Day extends NamedEntity {

    @ManyToMany
    @JoinTable(
        name = "day_times",
        joinColumns = @JoinColumn(name = "day_id"),
        inverseJoinColumns = @JoinColumn(name = "time_id")
    )
    private List<Time> times = new ArrayList<>();

    private int dayOfWeek; // 1 = Monday, ..., 7 = Sunday

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


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