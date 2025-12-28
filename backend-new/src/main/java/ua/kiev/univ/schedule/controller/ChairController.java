package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Chair;
import ua.kiev.univ.schedule.repository.ChairRepository;

import java.util.List;

@RestController
@RequestMapping("/api/chairs")
public class ChairController {

    @Autowired
    private ChairRepository chairRepository;

    @GetMapping
    public List<Chair> getAll() {
        return chairRepository.findAll();
    }

    @PostMapping
    public Chair create(@RequestBody Chair chair) {
        return chairRepository.save(chair);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        chairRepository.deleteById(id);
    }
}
