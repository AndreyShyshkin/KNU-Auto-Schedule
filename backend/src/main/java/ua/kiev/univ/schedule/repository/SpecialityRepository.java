package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.department.Speciality;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
}