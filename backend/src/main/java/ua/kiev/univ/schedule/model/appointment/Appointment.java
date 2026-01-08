package ua.kiev.univ.schedule.model.appointment;

import jakarta.persistence.*;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@jakarta.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "appointment_type")
@DiscriminatorValue("BASE")
public class Appointment extends ua.kiev.univ.schedule.model.core.Entity {

    @Transient
    private final Map<Date, List<Auditorium>> auditoriumMap = new HashMap<>();

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppointmentEntry> entries = new ArrayList<>();

    // Ссылки для работы алгоритма в памяти (не сохраняются в БД как FK)
    @Transient
    private Subject subject;
    @Transient
    private List<Teacher> teachers = new ArrayList<>();
    @Transient
    private List<Group> groups = new ArrayList<>();

    private boolean enable = true;

    // Снимки данных для автономности
    private String subjectName;
    private String teacherNames;
    private String groupNames;
    
    // Списки ID для фильтрации (храним как строки "1,2,3")
    @Column(length = 1000)
    private String teacherIds;
    @Column(length = 1000)
    private String groupIds;

    public Appointment() {}

    // Геттеры и сеттеры для совместимости с Executor
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }
    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { this.groups = groups; }
    public boolean isActive() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public Map<Date, List<Auditorium>> getAuditoriumMap() { return auditoriumMap; }
    public List<AppointmentEntry> getEntries() { return entries; }
    public void setEntries(List<AppointmentEntry> entries) { this.entries = entries; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getTeacherNames() { return teacherNames; }
    public void setTeacherNames(String teacherNames) { this.teacherNames = teacherNames; }
    public String getGroupNames() { return groupNames; }
    public void setGroupNames(String groupNames) { this.groupNames = groupNames; }
    public String getTeacherIds() { return teacherIds; }
    public void setTeacherIds(String teacherIds) { this.teacherIds = teacherIds; }
    public String getGroupIds() { return groupIds; }
    public void setGroupIds(String groupIds) { this.groupIds = groupIds; }

    @Override
    public void read(DataInputStream is) throws IOException {
        // Оставляем пустую реализацию или копируем старую, если нужно
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        // Оставляем пустую реализацию
    }
}