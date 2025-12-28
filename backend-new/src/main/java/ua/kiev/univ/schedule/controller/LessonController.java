package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.LessonDTO;
import ua.kiev.univ.schedule.entity.Lesson;
import ua.kiev.univ.schedule.entity.Group;
import ua.kiev.univ.schedule.entity.Teacher;
import ua.kiev.univ.schedule.entity.Subject;
import ua.kiev.univ.schedule.repository.LessonRepository;
import ua.kiev.univ.schedule.repository.GroupRepository;
import ua.kiev.univ.schedule.repository.TeacherRepository;
import ua.kiev.univ.schedule.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public List<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    @PostMapping
    public Lesson create(@RequestBody LessonDTO dto) {
        Lesson lesson = new Lesson();
        return updateLessonFromDto(lesson, dto);
    }
    
    @PutMapping("/{id}")
    public Lesson update(@PathVariable Long id, @RequestBody LessonDTO dto) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow();
        return updateLessonFromDto(lesson, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        lessonRepository.deleteById(id);
    }
    
    private Lesson updateLessonFromDto(Lesson lesson, LessonDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId()).orElse(null);
        List<Teacher> teachers = teacherRepository.findAllById(dto.getTeacherIds());
        List<Group> groups = groupRepository.findAllById(dto.getGroupIds());
        
        lesson.setSubject(subject);
        lesson.setLessonType(dto.getLessonType());
        lesson.setDurationHours(dto.getDurationHours());
        lesson.setTeachers(teachers);
        lesson.setGroups(groups);
        
        return lessonRepository.save(lesson);
    }
}
