package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Auditorium;
import ua.kiev.univ.schedule.repository.AuditoriumRepository;

import java.util.List;

@RestController
@RequestMapping("/api/auditoriums")
public class AuditoriumController {

    @Autowired
    private AuditoriumRepository auditoriumRepository;

    @GetMapping
    public List<Auditorium> getAll() {
        return auditoriumRepository.findAll();
    }

    @PostMapping
    public Auditorium create(@RequestBody Auditorium auditorium) {
        return auditoriumRepository.save(auditorium);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        auditoriumRepository.deleteById(id);
    }
}
