package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.date.Day;

public interface DayRepository extends JpaRepository<Day, Long> {
}