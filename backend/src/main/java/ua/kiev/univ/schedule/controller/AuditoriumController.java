package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.AuditoriumDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.repository.AuditoriumRepository;
import ua.kiev.univ.schedule.repository.EarmarkRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auditoriums")
public class AuditoriumController {

    private final AuditoriumRepository auditoriumRepository;
    private final EarmarkRepository earmarkRepository;

    public AuditoriumController(AuditoriumRepository auditoriumRepository, EarmarkRepository earmarkRepository) {
        this.auditoriumRepository = auditoriumRepository;
        this.earmarkRepository = earmarkRepository;
    }

    @GetMapping
    public List<AuditoriumDto> getAll() {
        return auditoriumRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<AuditoriumDto> create(@RequestBody AuditoriumDto dto) {
        Earmark earmark = null;
        if (dto.getEarmarkId() != null) {
            earmark = earmarkRepository.findById(dto.getEarmarkId()).orElse(null);
        }
        Auditorium auditorium = DtoMapper.toEntity(dto, earmark);
        return ResponseEntity.ok(DtoMapper.toDto(auditoriumRepository.save(auditorium)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditoriumDto> update(@PathVariable Long id, @RequestBody AuditoriumDto dto) {
        if (!auditoriumRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        Earmark earmark = null;
        if (dto.getEarmarkId() != null) {
            earmark = earmarkRepository.findById(dto.getEarmarkId()).orElse(null);
        }
        Auditorium auditorium = DtoMapper.toEntity(dto, earmark);
        return ResponseEntity.ok(DtoMapper.toDto(auditoriumRepository.save(auditorium)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!auditoriumRepository.existsById(id)) return ResponseEntity.notFound().build();
        auditoriumRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}