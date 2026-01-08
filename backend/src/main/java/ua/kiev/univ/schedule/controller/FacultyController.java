package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.FacultyDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.repository.FacultyRepository;

import java.util.List;
import java.util.stream.Collectors;

import ua.kiev.univ.schedule.service.core.DataInitializationService;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyRepository facultyRepository;
    private final DataInitializationService dataInitializationService;

    public FacultyController(FacultyRepository facultyRepository, DataInitializationService dataInitializationService) {
        this.facultyRepository = facultyRepository;
        this.dataInitializationService = dataInitializationService;
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
        Faculty saved = facultyRepository.save(faculty);
        dataInitializationService.initializeData();
        return DtoMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyDto> update(@PathVariable Long id, @RequestBody FacultyDto dto) {
        return facultyRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            Faculty saved = facultyRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!facultyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        facultyRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}