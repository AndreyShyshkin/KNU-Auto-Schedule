package ua.kiev.univ.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kiev.univ.schedule.dto.FacultyDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.repository.FacultyRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyRepository facultyRepository;

    public FacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public List<FacultyDto> getAll() {
        return facultyRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }
}