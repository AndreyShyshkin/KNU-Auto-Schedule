package ua.kiev.univ.schedule.model.appointment;

import jakarta.persistence.*;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.placement.Auditorium;

@Entity
@Table(name = "appointment_entry")
public class AppointmentEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Appointment appointment;

    @ManyToOne
    private Day day;

    @ManyToOne
    private Time timeSlot;

    @ManyToOne
    private Auditorium auditorium;

    public AppointmentEntry() {}

    public AppointmentEntry(Appointment appointment, Day day, Time timeSlot, Auditorium auditorium) {
        this.appointment = appointment;
        this.day = day;
        this.timeSlot = timeSlot;
        this.auditorium = auditorium;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public Day getDay() { return day; }
    public void setDay(Day day) { this.day = day; }
    public Time getTimeSlot() { return timeSlot; }
    public void setTimeSlot(Time timeSlot) { this.timeSlot = timeSlot; }
    public Auditorium getAuditorium() { return auditorium; }
    public void setAuditorium(Auditorium auditorium) { this.auditorium = auditorium; }
}