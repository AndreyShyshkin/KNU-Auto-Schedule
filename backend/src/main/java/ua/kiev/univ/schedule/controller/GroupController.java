package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.GroupDto;
import ua.kiev.univ.schedule.mapper.DtoMapper;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.repository.GroupRepository;
import ua.kiev.univ.schedule.repository.SpecialityRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final SpecialityRepository specialityRepository;
    private final DataInitializationService dataInitializationService;

    public GroupController(GroupRepository groupRepository, SpecialityRepository specialityRepository, DataInitializationService dataInitializationService) {
        this.groupRepository = groupRepository;
        this.specialityRepository = specialityRepository;
        this.dataInitializationService = dataInitializationService;
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
        Group saved = groupRepository.save(group);
        dataInitializationService.initializeData();
        return ResponseEntity.ok(DtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> update(@PathVariable Long id, @RequestBody GroupDto dto) {
        return groupRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setSize(dto.getSize());
            if (dto.getYear() > 0 && dto.getYear() <= ua.kiev.univ.schedule.model.member.Year.values().length) {
                existing.setYear(ua.kiev.univ.schedule.model.member.Year.values()[dto.getYear() - 1]);
            }
            if (dto.getDepartmentId() != null) {
                existing.setDepartment(specialityRepository.findById(dto.getDepartmentId()).orElse(null));
            }
            Group saved = groupRepository.save(existing);
            dataInitializationService.initializeData();
            return ResponseEntity.ok(DtoMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!groupRepository.existsById(id)) return ResponseEntity.notFound().build();
        groupRepository.deleteById(id);
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }
}