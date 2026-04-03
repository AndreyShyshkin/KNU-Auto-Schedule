package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.LessonDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.model.lesson.LessonType;
import ua.kiev.univ.schedule.repository.*;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final EarmarkRepository earmarkRepository;
    private final BuildingRepository buildingRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final LessonTypeRepository lessonTypeRepository;
    private final DataInitializationService dataInitializationService;

    public LessonController(LessonRepository lessonRepository, SubjectRepository subjectRepository, EarmarkRepository earmarkRepository, BuildingRepository buildingRepository, AuditoriumRepository auditoriumRepository, TeacherRepository teacherRepository, GroupRepository groupRepository, LessonTypeRepository lessonTypeRepository, DataInitializationService dataInitializationService) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.earmarkRepository = earmarkRepository;
        this.buildingRepository = buildingRepository;
        this.auditoriumRepository = auditoriumRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
        this.lessonTypeRepository = lessonTypeRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<LessonDto> getAll() {
        return lessonRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public LessonDto create(@RequestBody LessonDto dto) {
        Subject subject = dto.getSubjectId() != null ? subjectRepository.findById(dto.getSubjectId()).orElse(null) : null;
        Earmark earmark = dto.getEarmarkId() != null ? earmarkRepository.findById(dto.getEarmarkId()).orElse(null) : null;
        Building building = dto.getBuildingId() != null ? buildingRepository.findById(dto.getBuildingId()).orElse(null) : null;
        Auditorium auditorium = dto.getAuditoriumId() != null ? auditoriumRepository.findById(dto.getAuditoriumId()).orElse(null) : null;
        List<Teacher> teachers = dto.getTeacherIds() != null ? teacherRepository.findAllById(dto.getTeacherIds()) : List.of();
        List<Group> groups = dto.getGroupIds() != null ? groupRepository.findAllById(dto.getGroupIds()) : List.of();
        List<LessonType> lessonTypes = dto.getLessonTypeIds() != null ? lessonTypeRepository.findAllById(dto.getLessonTypeIds()) : List.of();

        Lesson lesson = DtoMapper.toEntity(dto, subject, earmark, building, auditorium, teachers, groups, lessonTypes);
        Lesson saved = lessonRepository.save(lesson);
        dataInitializationService.initializeData();
        return DtoMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDto> update(@PathVariable Long id, @RequestBody LessonDto dto) {
        return lessonRepository.findById(id).map(existing -> {
            Subject subject = dto.getSubjectId() != null ? subjectRepository.findById(dto.getSubjectId()).orElse(null) : null;
            Earmark earmark = dto.getEarmarkId() != null ? earmarkRepository.findById(dto.getEarmarkId()).orElse(null) : null;
            Building building = dto.getBuildingId() != null ? buildingRepository.findById(dto.getBuildingId()).orElse(null) : null;
            Auditorium auditorium = dto.getAuditoriumId() != null ? auditoriumRepository.findById(dto.getAuditoriumId()).orElse(null) : null;
            List<Teacher> teachers = dto.getTeacherIds() != null ? teacherRepository.findAllById(dto.getTeacherIds()) : List.of();
            List<Group> groups = dto.getGroupIds() != null ? groupRepository.findAllById(dto.getGroupIds()) : List.of();
            List<LessonType> lessonTypes = dto.getLessonTypeIds() != null ? lessonTypeRepository.findAllById(dto.getLessonTypeIds()) : List.of();

            existing.setSubject(subject);
            existing.setEarmark(earmark);
            existing.setBuilding(building);
            existing.setAuditorium(auditorium);
            existing.setTeachers(teachers);
            existing.setGroups(groups);
            existing.setLessonTypes(lessonTypes);
            existing.setCount(dto.getCount());
            existing.setOnline(dto.isOnline());
            existing.setOnlineLink(dto.getOnlineLink());

            Lesson saved = lessonRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!lessonRepository.existsById(id)) return ResponseEntity.notFound().build();
        lessonRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}
