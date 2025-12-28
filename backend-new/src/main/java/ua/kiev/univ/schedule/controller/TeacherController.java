package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Teacher;
import ua.kiev.univ.schedule.repository.TeacherRepository;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @PostMapping
    public Teacher create(@RequestBody Teacher teacher) {
        return teacherRepository.save(teacher);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teacherRepository.deleteById(id);
    }
}
