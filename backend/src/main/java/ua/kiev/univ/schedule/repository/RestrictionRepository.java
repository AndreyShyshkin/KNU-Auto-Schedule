package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.member.Restriction;

public interface RestrictionRepository extends JpaRepository<Restriction, Long> {
}
