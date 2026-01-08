package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.LessonDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final EarmarkRepository earmarkRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;

    public LessonController(LessonRepository lessonRepository, SubjectRepository subjectRepository,
                            EarmarkRepository earmarkRepository, TeacherRepository teacherRepository,
                            GroupRepository groupRepository) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.earmarkRepository = earmarkRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
    }

    @GetMapping
    public List<LessonDto> getAll() {
        return lessonRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<LessonDto> create(@RequestBody LessonDto dto) {
        Lesson lesson = new Lesson();
        updateLessonFromDto(lesson, dto);
        return ResponseEntity.ok(DtoMapper.toDto(lessonRepository.save(lesson)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDto> update(@PathVariable Long id, @RequestBody LessonDto dto) {
        if (!lessonRepository.existsById(id)) return ResponseEntity.notFound().build();
        Lesson lesson = lessonRepository.findById(id).orElseThrow();
        updateLessonFromDto(lesson, dto);
        return ResponseEntity.ok(DtoMapper.toDto(lessonRepository.save(lesson)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!lessonRepository.existsById(id)) return ResponseEntity.notFound().build();
        lessonRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void updateLessonFromDto(Lesson lesson, LessonDto dto) {
        lesson.setCount(dto.getCount());
        if (dto.getSubjectId() != null) {
            lesson.setSubject(subjectRepository.findById(dto.getSubjectId()).orElse(null));
        }
        if (dto.getEarmarkId() != null) {
            lesson.setEarmark(earmarkRepository.findById(dto.getEarmarkId()).orElse(null));
        }
        
        lesson.getTeachers().clear();
        if (dto.getTeacherIds() != null) {
            List<Teacher> teachers = teacherRepository.findAllById(dto.getTeacherIds());
            lesson.getTeachers().addAll(teachers);
        }

        lesson.getGroups().clear();
        if (dto.getGroupIds() != null) {
            List<Group> groups = groupRepository.findAllById(dto.getGroupIds());
            lesson.getGroups().addAll(groups);
        }
    }
}