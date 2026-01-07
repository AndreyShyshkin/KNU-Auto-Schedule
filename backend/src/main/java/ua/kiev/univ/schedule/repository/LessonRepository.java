package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.lesson.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}