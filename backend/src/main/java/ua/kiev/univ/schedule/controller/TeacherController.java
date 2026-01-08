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

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final ChairRepository chairRepository;

    public TeacherController(TeacherRepository teacherRepository, ChairRepository chairRepository) {
        this.teacherRepository = teacherRepository;
        this.chairRepository = chairRepository;
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
        return ResponseEntity.ok(DtoMapper.toDto(teacherRepository.save(teacher)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDto> update(@PathVariable Long id, @RequestBody TeacherDto dto) {
        if (!teacherRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        Chair chair = null;
        if (dto.getDepartmentId() != null) {
            chair = chairRepository.findById(dto.getDepartmentId()).orElse(null);
        }
        Teacher teacher = DtoMapper.toEntity(dto, chair);
        return ResponseEntity.ok(DtoMapper.toDto(teacherRepository.save(teacher)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!teacherRepository.existsById(id)) return ResponseEntity.notFound().build();
        teacherRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}