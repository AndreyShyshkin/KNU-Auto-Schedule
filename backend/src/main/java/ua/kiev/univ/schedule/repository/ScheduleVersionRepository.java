package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kiev.univ.schedule.model.appointment.ScheduleVersion;

import java.util.List;

@Repository
public interface ScheduleVersionRepository extends JpaRepository<ScheduleVersion, Long> {
    List<ScheduleVersion> findByOrderByCreatedAtDesc();
    ScheduleVersion findByIsCurrentTrue();
}