package ua.kiev.univ.schedule.model.date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ua.kiev.univ.schedule.model.core.EnablableEntity;
import ua.kiev.univ.schedule.util.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Entity
@Table(name = "time_slot")
public class Time extends EnablableEntity {

    @Column(name = "start_time")
    private String start = "";
    
    @Column(name = "end_time")
    private String end = "";

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        start = is.readUTF();
        end = is.readUTF();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        os.writeUTF(start);
        os.writeUTF(end);
    }

    @Override
    public boolean isActive() {
        // StringUtils буде підсвічуватися червоним, поки ми не створимо папку util
        return super.isActive() && !StringUtils.isBlank(start) && !StringUtils.isBlank(end);
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}