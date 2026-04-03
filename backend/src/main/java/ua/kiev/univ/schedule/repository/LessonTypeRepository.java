package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.lesson.LessonType;

public interface LessonTypeRepository extends JpaRepository<LessonType, Long> {
}
