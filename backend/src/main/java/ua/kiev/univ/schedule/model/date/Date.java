package ua.kiev.univ.schedule.model.date;

import ua.kiev.univ.schedule.model.core.ActivableEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Date extends ActivableEntity {

    private Day day;
    private Time time;

    public Date(DataInputStream is) throws IOException {
        read(is);
    }

    public Date(Day day, Time time) {
        this.day = day;
        this.time = time;
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        day = readEntity(Day.class, is);
        time = readEntity(Time.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(day, Day.class, os);
        writeEntity(time, Time.class, os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && day.isActive() && time.isActive();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Date)) return false;
        Date date = (Date) obj;
        
        Long d1 = (day != null) ? day.getId() : null;
        Long d2 = (date.getDay() != null) ? date.getDay().getId() : null;
        if (!Objects.equals(d1, d2)) return false;
        
        Long t1 = (time != null) ? time.getId() : null;
        Long t2 = (date.getTime() != null) ? date.getTime().getId() : null;
        return Objects.equals(t1, t2);
    }

    @Override
    public int hashCode() {
        Long dId = (day != null) ? day.getId() : null;
        Long tId = (time != null) ? time.getId() : null;
        return Objects.hash(dId, tId);
    }

    @Override
    public String toString() {
        return day.toString() + ": " + time.toString();
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}