package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.ChairDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.repository.ChairRepository;
import ua.kiev.univ.schedule.repository.FacultyRepository;

import java.util.List;
import java.util.stream.Collectors;

import ua.kiev.univ.schedule.service.core.DataInitializationService;

@RestController
@RequestMapping("/api/chairs")
public class ChairController {

    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;
    private final DataInitializationService dataInitializationService;

    public ChairController(ChairRepository chairRepository, FacultyRepository facultyRepository, DataInitializationService dataInitializationService) {
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<ChairDto> getAll() {
        return chairRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChairDto> getById(@PathVariable Long id) {
        return chairRepository.findById(id)
                .map(DtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChairDto> create(@RequestBody ChairDto dto) {
        Faculty faculty = null;
        if (dto.getFacultyId() != null) {
            faculty = facultyRepository.findById(dto.getFacultyId()).orElse(null);
        }
        Chair chair = DtoMapper.toEntity(dto, faculty);
        Chair saved = chairRepository.save(chair);
        dataInitializationService.initializeData();
        return ResponseEntity.ok(DtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChairDto> update(@PathVariable Long id, @RequestBody ChairDto dto) {
        return chairRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            if (dto.getFacultyId() != null) {
                existing.setFaculty(facultyRepository.findById(dto.getFacultyId()).orElse(null));
            }
            Chair saved = chairRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!chairRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        chairRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}