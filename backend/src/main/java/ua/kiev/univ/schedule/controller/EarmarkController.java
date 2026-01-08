package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.EarmarkDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.repository.EarmarkRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/earmarks")
public class EarmarkController {

    private final EarmarkRepository earmarkRepository;
    private final DataInitializationService dataInitializationService;

    public EarmarkController(EarmarkRepository earmarkRepository, DataInitializationService dataInitializationService) {
        this.earmarkRepository = earmarkRepository;
        this.dataInitializationService = dataInitializationService;
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
        Earmark saved = earmarkRepository.save(earmark);
        dataInitializationService.initializeData();
        return DtoMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EarmarkDto> update(@PathVariable Long id, @RequestBody EarmarkDto dto) {
        return earmarkRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setSize(dto.getSize());
            Earmark saved = earmarkRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!earmarkRepository.existsById(id)) return ResponseEntity.notFound().build();
        earmarkRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}