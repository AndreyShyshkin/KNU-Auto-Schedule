package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Speciality;
import ua.kiev.univ.schedule.repository.SpecialityRepository;

import java.util.List;

@RestController
@RequestMapping("/api/specialities")
public class SpecialityController {

    @Autowired
    private SpecialityRepository specialityRepository;

    @GetMapping
    public List<Speciality> getAll() {
        return specialityRepository.findAll();
    }

    @PostMapping
    public Speciality create(@RequestBody Speciality speciality) {
        return specialityRepository.save(speciality);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        specialityRepository.deleteById(id);
    }
}
