package ua.kiev.univ.schedule.model.date;

import ua.kiev.univ.schedule.model.core.NamedEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Day extends NamedEntity {

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