package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kiev.univ.schedule.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {}
