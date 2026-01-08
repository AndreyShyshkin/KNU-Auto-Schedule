package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.EarmarkDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.repository.EarmarkRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/earmarks")
public class EarmarkController {

    private final EarmarkRepository earmarkRepository;

    public EarmarkController(EarmarkRepository earmarkRepository) {
        this.earmarkRepository = earmarkRepository;
    }

    @GetMapping
    public List<EarmarkDto> getAll() {
        return earmarkRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public EarmarkDto create(@RequestBody EarmarkDto dto) {
        Earmark earmark = DtoMapper.toEntity(dto);
        return DtoMapper.toDto(earmarkRepository.save(earmark));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EarmarkDto> update(@PathVariable Long id, @RequestBody EarmarkDto dto) {
        if (!earmarkRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        Earmark earmark = DtoMapper.toEntity(dto);
        return ResponseEntity.ok(DtoMapper.toDto(earmarkRepository.save(earmark)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!earmarkRepository.existsById(id)) return ResponseEntity.notFound().build();
        earmarkRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}