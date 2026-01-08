package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.TeacherDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.repository.ChairRepository;
import ua.kiev.univ.schedule.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

import ua.kiev.univ.schedule.service.core.DataInitializationService;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final ChairRepository chairRepository;
    private final DataInitializationService dataInitializationService;

    public TeacherController(TeacherRepository teacherRepository, ChairRepository chairRepository, DataInitializationService dataInitializationService) {
        this.teacherRepository = teacherRepository;
        this.chairRepository = chairRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<TeacherDto> getAll() {
        return teacherRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<TeacherDto> create(@RequestBody TeacherDto dto) {
        Chair chair = null;
        if (dto.getDepartmentId() != null) {
            chair = chairRepository.findById(dto.getDepartmentId()).orElse(null);
        }
        Teacher teacher = DtoMapper.toEntity(dto, chair);
        Teacher saved = teacherRepository.save(teacher);
        dataInitializationService.initializeData();
        return ResponseEntity.ok(DtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDto> update(@PathVariable Long id, @RequestBody TeacherDto dto) {
        return teacherRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            if (dto.getDepartmentId() != null) {
                existing.setDepartment(chairRepository.findById(dto.getDepartmentId()).orElse(null));
            }
            Teacher saved = teacherRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!teacherRepository.existsById(id)) return ResponseEntity.notFound().build();
        teacherRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}