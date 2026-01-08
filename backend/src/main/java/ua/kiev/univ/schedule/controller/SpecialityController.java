package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.SpecialityDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.repository.FacultyRepository;
import ua.kiev.univ.schedule.repository.SpecialityRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/specialities")
public class SpecialityController {

    private final SpecialityRepository specialityRepository;
    private final FacultyRepository facultyRepository;
    private final DataInitializationService dataInitializationService;

    public SpecialityController(SpecialityRepository specialityRepository, FacultyRepository facultyRepository, DataInitializationService dataInitializationService) {
        this.specialityRepository = specialityRepository;
        this.facultyRepository = facultyRepository;
        this.dataInitializationService = dataInitializationService;
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
        Speciality saved = specialityRepository.save(speciality);
        dataInitializationService.initializeData();
        return ResponseEntity.ok(DtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialityDto> update(@PathVariable Long id, @RequestBody SpecialityDto dto) {
        return specialityRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            if (dto.getFacultyId() != null) {
                existing.setFaculty(facultyRepository.findById(dto.getFacultyId()).orElse(null));
            }
            Speciality saved = specialityRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!specialityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        specialityRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}