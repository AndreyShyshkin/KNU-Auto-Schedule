package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.FacultyDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.repository.FacultyRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyRepository facultyRepository;

    public FacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public List<FacultyDto> getAll() {
        return facultyRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public FacultyDto create(@RequestBody FacultyDto dto) {
        Faculty faculty = DtoMapper.toEntity(dto);
        return DtoMapper.toDto(facultyRepository.save(faculty));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyDto> update(@PathVariable Long id, @RequestBody FacultyDto dto) {
        if (!facultyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dto.setId(id);
        Faculty faculty = DtoMapper.toEntity(dto);
        return ResponseEntity.ok(DtoMapper.toDto(facultyRepository.save(faculty)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!facultyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        facultyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}