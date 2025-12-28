package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Subject;
import ua.kiev.univ.schedule.repository.SubjectRepository;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @PostMapping
    public Subject create(@RequestBody Subject subject) {
        return subjectRepository.save(subject);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subjectRepository.deleteById(id);
    }
}
