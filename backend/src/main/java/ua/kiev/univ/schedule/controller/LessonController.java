package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.LessonDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.repository.*;
import ua.kiev.univ.schedule.service.core.DataInitializationService;
import org.springframework.jdbc.core.JdbcTemplate;

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
    private final DataInitializationService dataInitializationService;
    private final JdbcTemplate jdbcTemplate;

    public LessonController(LessonRepository lessonRepository, SubjectRepository subjectRepository,
                            EarmarkRepository earmarkRepository, TeacherRepository teacherRepository,
                            GroupRepository groupRepository, DataInitializationService dataInitializationService,
                            JdbcTemplate jdbcTemplate) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.earmarkRepository = earmarkRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
        this.dataInitializationService = dataInitializationService;
        this.jdbcTemplate = jdbcTemplate;
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
        Lesson saved = lessonRepository.save(lesson);
        dataInitializationService.initializeData();
        return ResponseEntity.ok(DtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDto> update(@PathVariable Long id, @RequestBody LessonDto dto) {
        return lessonRepository.findById(id).map(existing -> {
            updateLessonFromDto(existing, dto);
            Lesson saved = lessonRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return lessonRepository.findById(id).map(lesson -> {
            // Manually clear join tables for this specific lesson
            jdbcTemplate.execute("DELETE FROM lesson_teachers WHERE lesson_id = " + id);
            jdbcTemplate.execute("DELETE FROM lesson_groups WHERE lesson_id = " + id);
            
            lessonRepository.delete(lesson);
            lessonRepository.flush();
            
            dataInitializationService.initializeData();
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
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