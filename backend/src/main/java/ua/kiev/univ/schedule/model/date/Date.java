package ua.kiev.univ.schedule.model.date;

import ua.kiev.univ.schedule.model.core.ActivableEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Date extends ActivableEntity {

    private Day day;
    private Time time;
    private LocalDate localDate;

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
        if (is.readBoolean()) {
            localDate = LocalDate.ofEpochDay(is.readLong());
        }
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(day, Day.class, os);
        writeEntity(time, Time.class, os);
        os.writeBoolean(localDate != null);
        if (localDate != null) {
            os.writeLong(localDate.toEpochDay());
        }
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
        
        if (!Objects.equals(localDate, date.localDate)) return false;

        Long d1 = (day != null) ? day.getId() : null;
        Long d2 = (date.getDay() != null) ? date.getDay().getId() : null;
        if (!Objects.equals(d1, d2)) return false;
        
        Long t1 = (time != null) ? time.getId() : null;
        Long t2 = (date.getTime() != null) ? date.getTime().getId() : null;
        return Objects.equals(t1, t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day != null ? day.getId() : null, time != null ? time.getId() : null, localDate);
    }

    @Override
    public String toString() {
        String base = day.toString() + ": " + time.toString();
        if (localDate != null) {
            return localDate.format(DateTimeFormatter.ofPattern("dd.MM")) + " " + base;
        }
        return base;
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

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}