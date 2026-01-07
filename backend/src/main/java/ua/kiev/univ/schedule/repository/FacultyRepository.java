package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.department.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}