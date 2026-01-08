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

@RestController
@RequestMapping("/api/chairs")
public class ChairController {

    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;

    public ChairController(ChairRepository chairRepository, FacultyRepository facultyRepository) {
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
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
        return ResponseEntity.ok(DtoMapper.toDto(chairRepository.save(chair)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChairDto> update(@PathVariable Long id, @RequestBody ChairDto dto) {
        if (!chairRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dto.setId(id);
        Faculty faculty = null;
        if (dto.getFacultyId() != null) {
            faculty = facultyRepository.findById(dto.getFacultyId()).orElse(null);
        }
        Chair chair = DtoMapper.toEntity(dto, faculty);
        return ResponseEntity.ok(DtoMapper.toDto(chairRepository.save(chair)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!chairRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        chairRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}