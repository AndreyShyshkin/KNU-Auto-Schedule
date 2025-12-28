package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Faculty;
import ua.kiev.univ.schedule.repository.FacultyRepository;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    @Autowired
    private FacultyRepository facultyRepository;

    @GetMapping
    public List<Faculty> getAll() {
        return facultyRepository.findAll();
    }

    @PostMapping
    public Faculty create(@RequestBody Faculty faculty) {
        return facultyRepository.save(faculty);
    }
}
