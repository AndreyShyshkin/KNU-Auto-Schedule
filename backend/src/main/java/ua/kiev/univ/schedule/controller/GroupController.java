package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.GroupDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.repository.GroupRepository;
import ua.kiev.univ.schedule.repository.SpecialityRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final SpecialityRepository specialityRepository;

    public GroupController(GroupRepository groupRepository, SpecialityRepository specialityRepository) {
        this.groupRepository = groupRepository;
        this.specialityRepository = specialityRepository;
    }

    @GetMapping
    public List<GroupDto> getAll() {
        return groupRepository.findAll().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<GroupDto> create(@RequestBody GroupDto dto) {
        Speciality speciality = null;
        if (dto.getDepartmentId() != null) {
            speciality = specialityRepository.findById(dto.getDepartmentId()).orElse(null);
        }
        Group group = DtoMapper.toEntity(dto, speciality);
        return ResponseEntity.ok(DtoMapper.toDto(groupRepository.save(group)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> update(@PathVariable Long id, @RequestBody GroupDto dto) {
        if (!groupRepository.existsById(id)) return ResponseEntity.notFound().build();
        dto.setId(id);
        Speciality speciality = null;
        if (dto.getDepartmentId() != null) {
            speciality = specialityRepository.findById(dto.getDepartmentId()).orElse(null);
        }
        Group group = DtoMapper.toEntity(dto, speciality);
        return ResponseEntity.ok(DtoMapper.toDto(groupRepository.save(group)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!groupRepository.existsById(id)) return ResponseEntity.notFound().build();
        groupRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}