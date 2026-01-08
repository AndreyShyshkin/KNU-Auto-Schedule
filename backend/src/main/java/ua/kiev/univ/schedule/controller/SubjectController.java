package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.SubjectDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
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
        return DtoMapper.toDto(subjectRepository.save(subject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDto> update(@PathVariable Long id, @RequestBody SubjectDto dto) {
        if (!subjectRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        Subject subject = DtoMapper.toEntity(dto);
        return ResponseEntity.ok(DtoMapper.toDto(subjectRepository.save(subject)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!subjectRepository.existsById(id)) return ResponseEntity.notFound().build();
        subjectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}