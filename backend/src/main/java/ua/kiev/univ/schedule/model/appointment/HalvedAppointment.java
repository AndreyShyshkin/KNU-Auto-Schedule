package ua.kiev.univ.schedule.model.appointment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

@Entity
@DiscriminatorValue("HALVED")
public class HalvedAppointment extends Appointment {

    @Transient
    private Date date;
    @Transient
    private List<Auditorium> auditoriums;
    @Transient
    private Part part;

    // Snapshot fields
    private String halvedDayName;
    private String halvedTimeRange;
    private String halvedAuditoriumNames;
    private String halvedPartName;

    public String getHalvedDayName() { return halvedDayName; }
    public void setHalvedDayName(String s) { this.halvedDayName = s; }
    public String getHalvedTimeRange() { return halvedTimeRange; }
    public void setHalvedTimeRange(String s) { this.halvedTimeRange = s; }
    public String getHalvedAuditoriumNames() { return halvedAuditoriumNames; }
    public void setHalvedAuditoriumNames(String s) { this.halvedAuditoriumNames = s; }
    public String getHalvedPartName() { return halvedPartName; }
    public void setHalvedPartName(String s) { this.halvedPartName = s; }

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