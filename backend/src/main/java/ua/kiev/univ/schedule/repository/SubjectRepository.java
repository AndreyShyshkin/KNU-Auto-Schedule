package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.subject.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}