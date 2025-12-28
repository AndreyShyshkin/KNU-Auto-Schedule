package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kiev.univ.schedule.entity.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {}
