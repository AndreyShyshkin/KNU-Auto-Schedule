package ua.kiev.univ.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.entity.Group;
import ua.kiev.univ.schedule.repository.GroupRepository;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    @PostMapping
    public Group create(@RequestBody Group group) {
        return groupRepository.save(group);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupRepository.deleteById(id);
    }
}
