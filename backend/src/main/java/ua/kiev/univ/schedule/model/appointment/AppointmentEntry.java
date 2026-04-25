package ua.kiev.univ.schedule.model.appointment;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_entry")
public class AppointmentEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Appointment appointment;

    private String dayName;
    private String timeStart;
    private String timeEnd;
    private String buildingName;
    private String auditoriumName;
    
    private String teacherNames;
    private String groupNames;

    public AppointmentEntry() {}

    public AppointmentEntry(Appointment appointment, String dayName, String timeStart, String timeEnd, String buildingName, String auditoriumName) {
        this.appointment = appointment;
        this.dayName = dayName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.buildingName = buildingName;
        this.auditoriumName = auditoriumName;
    }

    public AppointmentEntry(Appointment appointment, String dayName, String timeStart, String timeEnd, String buildingName, String auditoriumName, String teacherNames, String groupNames) {
        this(appointment, dayName, timeStart, timeEnd, buildingName, auditoriumName);
        this.teacherNames = teacherNames;
        this.groupNames = groupNames;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }
    public String getTimeStart() { return timeStart; }
    public void setTimeStart(String timeStart) { this.timeStart = timeStart; }
    public String getTimeEnd() { return timeEnd; }
    public void setTimeEnd(String timeEnd) { this.timeEnd = timeEnd; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getAuditoriumName() { return auditoriumName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public String getTeacherNames() { return teacherNames; }
    public void setTeacherNames(String teacherNames) { this.teacherNames = teacherNames; }
    public String getGroupNames() { return groupNames; }
    public void setGroupNames(String groupNames) { this.groupNames = groupNames; }
}
