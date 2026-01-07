package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.placement.Earmark;

public interface EarmarkRepository extends JpaRepository<Earmark, Long> {
}