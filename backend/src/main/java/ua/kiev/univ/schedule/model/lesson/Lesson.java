package ua.kiev.univ.schedule.model.lesson;

import jakarta.persistence.*;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeEntity(earmark, Earmark.class, os);
        writeEntity(building, Building.class, os);
        writeEntity(auditorium, Auditorium.class, os);
        os.writeBoolean(online);
        os.writeUTF(onlineLink != null ? onlineLink : "");
        os.writeInt(count);
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
}
