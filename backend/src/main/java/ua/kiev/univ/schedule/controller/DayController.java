package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.DayDto;
import ua.kiev.univ.schedule.dto.TimeDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.repository.DayRepository;
import ua.kiev.univ.schedule.repository.TimeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/days")
public class DayController {

    private final DayRepository dayRepository;
    private final TimeRepository timeRepository;

    public DayController(DayRepository dayRepository, TimeRepository timeRepository) {
        this.dayRepository = dayRepository;
        this.timeRepository = timeRepository;
    }

    @GetMapping
    public List<DayDto> getAll() {
        return dayRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<DayDto> create(@RequestBody DayDto dto) {
        List<Time> times = resolveTimes(dto.getTimes());
        Day day = DtoMapper.toEntity(dto, times);
        return ResponseEntity.ok(DtoMapper.toDto(dayRepository.save(day)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DayDto> update(@PathVariable Long id, @RequestBody DayDto dto) {
        if (!dayRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        List<Time> times = resolveTimes(dto.getTimes());
        Day day = DtoMapper.toEntity(dto, times);
        return ResponseEntity.ok(DtoMapper.toDto(dayRepository.save(day)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!dayRepository.existsById(id)) return ResponseEntity.notFound().build();
        dayRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private List<Time> resolveTimes(List<TimeDto> timeDtos) {
        if (timeDtos == null) return new ArrayList<>();
        List<Long> ids = timeDtos.stream().map(TimeDto::getId).collect(Collectors.toList());
        return timeRepository.findAllById(ids);
    }
}