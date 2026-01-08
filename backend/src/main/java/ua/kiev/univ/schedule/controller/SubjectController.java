package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.SubjectDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.repository.SubjectRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final DataInitializationService dataInitializationService;

    public SubjectController(SubjectRepository subjectRepository, DataInitializationService dataInitializationService) {
        this.subjectRepository = subjectRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<SubjectDto> getAll() {
        return subjectRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public SubjectDto create(@RequestBody SubjectDto dto) {
        Subject subject = DtoMapper.toEntity(dto);
        Subject saved = subjectRepository.save(subject);
        dataInitializationService.initializeData();
        return DtoMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDto> update(@PathVariable Long id, @RequestBody SubjectDto dto) {
        return subjectRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            Subject saved = subjectRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!subjectRepository.existsById(id)) return ResponseEntity.notFound().build();
        subjectRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}