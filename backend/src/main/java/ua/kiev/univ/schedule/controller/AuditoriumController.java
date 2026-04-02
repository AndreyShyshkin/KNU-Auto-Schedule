package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.AuditoriumDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.repository.AuditoriumRepository;
import ua.kiev.univ.schedule.repository.BuildingRepository;
import ua.kiev.univ.schedule.repository.EarmarkRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auditoriums")
public class AuditoriumController {

    private final AuditoriumRepository auditoriumRepository;
    private final EarmarkRepository earmarkRepository;
    private final BuildingRepository buildingRepository;
    private final ua.kiev.univ.schedule.service.core.DataInitializationService dataInitializationService;

    public AuditoriumController(AuditoriumRepository auditoriumRepository, EarmarkRepository earmarkRepository, BuildingRepository buildingRepository, ua.kiev.univ.schedule.service.core.DataInitializationService dataInitializationService) {
        this.auditoriumRepository = auditoriumRepository;
        this.earmarkRepository = earmarkRepository;
        this.buildingRepository = buildingRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<AuditoriumDto> getAll() {
        return auditoriumRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<AuditoriumDto> create(@RequestBody AuditoriumDto dto) {
        Earmark earmark = dto.getEarmarkId() != null ? earmarkRepository.findById(dto.getEarmarkId()).orElse(null) : null;
        Building building = dto.getBuildingId() != null ? buildingRepository.findById(dto.getBuildingId()).orElse(null) : null;
        Auditorium auditorium = DtoMapper.toEntity(dto, earmark, building);
        Auditorium saved = auditoriumRepository.save(auditorium);
        dataInitializationService.initializeData();
        return ResponseEntity.ok(DtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditoriumDto> update(@PathVariable Long id, @RequestBody AuditoriumDto dto) {
        return auditoriumRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            if (dto.getEarmarkId() != null) {
                existing.setEarmark(earmarkRepository.findById(dto.getEarmarkId()).orElse(null));
            }
            if (dto.getBuildingId() != null) {
                existing.setBuilding(buildingRepository.findById(dto.getBuildingId()).orElse(null));
            } else {
                existing.setBuilding(null);
            }
            Auditorium saved = auditoriumRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!auditoriumRepository.existsById(id)) return ResponseEntity.notFound().build();
        auditoriumRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}