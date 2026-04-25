package ua.kiev.univ.schedule.model.appointment;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_version")
public class ScheduleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("current")
    private boolean isCurrent;

    private LocalDate startDate;
    private LocalDate endDate;

    public ScheduleVersion() {
    }

    public ScheduleVersion(String name, LocalDateTime createdAt, boolean isCurrent) {
        this.name = name;
        this.createdAt = createdAt;
        this.isCurrent = isCurrent;
    }

    public ScheduleVersion(String name, LocalDateTime createdAt, boolean isCurrent, LocalDate startDate, LocalDate endDate) {
        this(name, createdAt, isCurrent);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
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
}