package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.BuildingDto;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.repository.BuildingRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingRepository repository;
    private final DataInitializationService dataInitializationService;

    public BuildingController(BuildingRepository repository, DataInitializationService dataInitializationService) {
        this.repository = repository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<BuildingDto> getAll() {
        return repository.findAll().stream()
                .map(b -> new BuildingDto(b.getId(), b.getName(), b.getDescription()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public BuildingDto create(@RequestBody BuildingDto dto) {
        Building b = new Building();
        b.setName(dto.getName());
        b.setDescription(dto.getDescription());
        b = repository.save(b);
        dataInitializationService.initializeData();
        return new BuildingDto(b.getId(), b.getName(), b.getDescription());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingDto> update(@PathVariable Long id, @RequestBody BuildingDto dto) {
        return repository.findById(id).map(b -> {
            b.setName(dto.getName());
            b.setDescription(dto.getDescription());
            b = repository.save(b);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(new BuildingDto(b.getId(), b.getName(), b.getDescription()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            dataInitializationService.initializeData();
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
