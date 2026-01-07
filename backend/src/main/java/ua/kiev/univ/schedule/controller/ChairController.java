package ua.kiev.univ.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kiev.univ.schedule.dto.ChairDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.repository.ChairRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chairs")
public class ChairController {

    private final ChairRepository chairRepository;

    public ChairController(ChairRepository chairRepository) {
        this.chairRepository = chairRepository;
    }

    @GetMapping
    public List<ChairDto> getAll() {
        return chairRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public ChairDto getById(@PathVariable Long id) {
        return chairRepository.findById(id)
                .map(DtoMapper::toDto)
                .orElse(null); // Or throw 404
    }
}