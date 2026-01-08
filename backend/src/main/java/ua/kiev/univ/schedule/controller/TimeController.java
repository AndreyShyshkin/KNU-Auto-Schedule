package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.TimeDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.repository.TimeRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/times")
public class TimeController {

    private final TimeRepository timeRepository;

    public TimeController(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    @GetMapping
    public List<TimeDto> getAll() {
        return timeRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TimeDto create(@RequestBody TimeDto dto) {
        Time time = DtoMapper.toEntity(dto);
        return DtoMapper.toDto(timeRepository.save(time));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeDto> update(@PathVariable Long id, @RequestBody TimeDto dto) {
        if (!timeRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        Time time = DtoMapper.toEntity(dto);
        return ResponseEntity.ok(DtoMapper.toDto(timeRepository.save(time)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!timeRepository.existsById(id)) return ResponseEntity.notFound().build();
        timeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}