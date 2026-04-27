package ua.kiev.univ.schedule.model.lesson;

import jakarta.persistence.*;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Lesson extends MemberedEntity {

    @ManyToOne
    private Earmark earmark;

    @ManyToOne
    private Building building;

    @ManyToOne
    private Auditorium auditorium;

    @ManyToMany
    @JoinTable(
        name = "lesson_lesson_types",
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "lesson_type_id")
    )
    private List<LessonType> lessonTypes = new ArrayList<>();

    private boolean online = false;

    @Column(length = 1000)
    private String onlineLink = "";

    private Integer count = 2;

    private Integer totalHours = 30;
    private LocalDate startDate;
    private LocalDate endDate;
    
    private Integer weekFrequency = 0; // 0=Every week, 1=Odd, 2=Even

    @Column(name = "allow_multiple_auditoriums", nullable = false, columnDefinition = "boolean default false")
    private Boolean allowMultipleAuditoriums = false;

    @Override
    public boolean isActive() {
        return super.isActive() && (online || (earmark != null && earmark.isActive()));
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        earmark = readEntity(Earmark.class, is);
        building = readEntity(Building.class, is);
        auditorium = readEntity(Auditorium.class, is);
        online = is.readBoolean();
        onlineLink = is.readUTF();
        count = is.readInt();
        allowMultipleAuditoriums = is.readBoolean();
        
        totalHours = is.readInt();
        if (is.readBoolean()) {
            startDate = LocalDate.ofEpochDay(is.readLong());
        }
        if (is.readBoolean()) {
            endDate = LocalDate.ofEpochDay(is.readLong());
        }
        weekFrequency = is.readInt();
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(earmark, Earmark.class, os);
        writeEntity(building, Building.class, os);
        writeEntity(auditorium, Auditorium.class, os);
        os.writeBoolean(online);
        os.writeUTF(onlineLink != null ? onlineLink : "");
        os.writeInt(count != null ? count : 2);
        os.writeBoolean(allowMultipleAuditoriums != null ? allowMultipleAuditoriums : false);
        
        os.writeInt(totalHours != null ? totalHours : 0);
        os.writeBoolean(startDate != null);
        if (startDate != null) {
            os.writeLong(startDate.toEpochDay());
        }
        os.writeBoolean(endDate != null);
        if (endDate != null) {
            os.writeLong(endDate.toEpochDay());
        }
        os.writeInt(weekFrequency != null ? weekFrequency : 0);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getOnlineLink() {
        return onlineLink;
    }

    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
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

    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public List<LessonType> getLessonTypes() {
        return lessonTypes;
    }

    public void setLessonTypes(List<LessonType> lessonTypes) {
        this.lessonTypes = lessonTypes;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getWeekFrequency() {
        return weekFrequency;
    }

    public void setWeekFrequency(Integer weekFrequency) {
        this.weekFrequency = weekFrequency;
    }

    public Boolean isAllowMultipleAuditoriums() {
        return allowMultipleAuditoriums;
    }

    public void setAllowMultipleAuditoriums(Boolean allowMultipleAuditoriums) {
        this.allowMultipleAuditoriums = allowMultipleAuditoriums;
    }
}
