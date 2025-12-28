package ua.kiev.univ.schedule.model.appointment;

import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class HalvedAppointment extends Appointment {

    private Date date;
    private List<Auditorium> auditoriums;
    private Part part;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        date = new Date(is);
        auditoriums = readList(Auditorium.class, is);
        part = Part.values()[is.readInt()];
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        date.write(os);
        writeList(auditoriums, Auditorium.class, os);
        os.writeInt(part.ordinal());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Auditorium> getAuditoriums() {
        return auditoriums;
    }

    public void setAuditoriums(List<Auditorium> auditoriums) {
        this.auditoriums = auditoriums;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
}