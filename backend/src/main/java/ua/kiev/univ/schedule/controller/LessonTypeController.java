package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.LessonTypeDto;
import ua.kiev.univ.schedule.model.lesson.LessonType;
import ua.kiev.univ.schedule.repository.LessonTypeRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lesson-types")
public class LessonTypeController {

    private final LessonTypeRepository repository;
    private final DataInitializationService dataInitializationService;

    public LessonTypeController(LessonTypeRepository repository, DataInitializationService dataInitializationService) {
        this.repository = repository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<LessonTypeDto> getAll() {
        return repository.findAll().stream()
                .map(t -> new LessonTypeDto(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public LessonTypeDto create(@RequestBody LessonTypeDto dto) {
        LessonType t = new LessonType();
        t.setName(dto.getName());
        t = repository.save(t);
        dataInitializationService.initializeData();
        return new LessonTypeDto(t.getId(), t.getName());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonTypeDto> update(@PathVariable Long id, @RequestBody LessonTypeDto dto) {
        return repository.findById(id).map(t -> {
            t.setName(dto.getName());
            t = repository.save(t);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(new LessonTypeDto(t.getId(), t.getName()));
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
