package ua.kiev.univ.schedule.model.appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.MemberedEntity;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class Appointment extends MemberedEntity {

    @Transient
    private final Map<Date, List<Auditorium>> auditoriumMap = new HashMap<>();

    public Map<Date, List<Auditorium>> getAuditoriumMap() {
        return auditoriumMap;
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        int size = is.readInt();
        while (size-- > 0) {
            Date date = new Date(is);
            List<Auditorium> auditoriums = readList(Auditorium.class, is);
            auditoriumMap.put(date, auditoriums);
        }
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        Set<Date> dates = auditoriumMap.keySet();
        os.writeInt(dates.size());
        for (Date date : dates) {
            date.write(os);
            List<Auditorium> auditoriums = auditoriumMap.get(date);
            writeList(auditoriums, Auditorium.class, os);
        }
    }
}