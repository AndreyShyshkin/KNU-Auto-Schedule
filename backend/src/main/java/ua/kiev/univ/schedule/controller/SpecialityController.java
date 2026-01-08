package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.SpecialityDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.repository.FacultyRepository;
import ua.kiev.univ.schedule.repository.SpecialityRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/specialities")
public class SpecialityController {

    private final SpecialityRepository specialityRepository;
    private final FacultyRepository facultyRepository;

    public SpecialityController(SpecialityRepository specialityRepository, FacultyRepository facultyRepository) {
        this.specialityRepository = specialityRepository;
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public List<SpecialityDto> getAll() {
        return specialityRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SpecialityDto> create(@RequestBody SpecialityDto dto) {
        Faculty faculty = null;
        if (dto.getFacultyId() != null) {
            faculty = facultyRepository.findById(dto.getFacultyId()).orElse(null);
        }
        Speciality speciality = DtoMapper.toEntity(dto, faculty);
        return ResponseEntity.ok(DtoMapper.toDto(specialityRepository.save(speciality)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialityDto> update(@PathVariable Long id, @RequestBody SpecialityDto dto) {
        if (!specialityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dto.setId(id);
        Faculty faculty = null;
        if (dto.getFacultyId() != null) {
            faculty = facultyRepository.findById(dto.getFacultyId()).orElse(null);
        }
        Speciality speciality = DtoMapper.toEntity(dto, faculty);
        return ResponseEntity.ok(DtoMapper.toDto(specialityRepository.save(speciality)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!specialityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        specialityRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}