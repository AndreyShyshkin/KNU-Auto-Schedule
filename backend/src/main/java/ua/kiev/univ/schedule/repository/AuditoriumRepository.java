package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.placement.Auditorium;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
}