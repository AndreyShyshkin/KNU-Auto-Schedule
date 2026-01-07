package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.member.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}