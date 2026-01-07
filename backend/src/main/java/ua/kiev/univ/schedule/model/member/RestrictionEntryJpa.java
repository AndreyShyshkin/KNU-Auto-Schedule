package ua.kiev.univ.schedule.model.member;

import jakarta.persistence.*;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;

@Entity
@Table(name = "restriction_entry")
public class RestrictionEntryJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Restriction restriction;

    @ManyToOne
    private Day day;

    @ManyToOne
    private Time timeSlot;

    @Enumerated(EnumType.ORDINAL)
    private Grade grade;

    public RestrictionEntryJpa() {}

    public RestrictionEntryJpa(Restriction restriction, Day day, Time timeSlot, Grade grade) {
        this.restriction = restriction;
        this.day = day;
        this.timeSlot = timeSlot;
        this.grade = grade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Restriction getRestriction() { return restriction; }
    public void setRestriction(Restriction restriction) { this.restriction = restriction; }
    public Day getDay() { return day; }
    public void setDay(Day day) { this.day = day; }
    public Time getTimeSlot() { return timeSlot; }
    public void setTimeSlot(Time timeSlot) { this.timeSlot = timeSlot; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
}