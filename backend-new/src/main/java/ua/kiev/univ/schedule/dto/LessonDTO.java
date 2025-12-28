package ua.kiev.univ.schedule.dto;

import lombok.Data;
import java.util.List;

@Data
public class LessonDTO {
    private Long id;
    private Long subjectId;
    private String lessonType; // Lecture, Practical
    private Integer durationHours;
    private List<Long> teacherIds;
    private List<Long> groupIds;
}
