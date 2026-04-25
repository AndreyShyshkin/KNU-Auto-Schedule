package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.TeacherRestrictionDto;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.member.Grade;
import ua.kiev.univ.schedule.model.member.Restriction;
import ua.kiev.univ.schedule.model.member.RestrictionEntryJpa;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.repository.DayRepository;
import ua.kiev.univ.schedule.repository.TeacherRepository;
import ua.kiev.univ.schedule.repository.TimeRepository;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers/{teacherId}/restrictions")
public class TeacherRestrictionController {

    private final TeacherRepository teacherRepository;
    private final DayRepository dayRepository;
    private final TimeRepository timeRepository;
    private final DataInitializationService dataInitializationService;

    public TeacherRestrictionController(TeacherRepository teacherRepository,
                                        DayRepository dayRepository,
                                        TimeRepository timeRepository,
                                        DataInitializationService dataInitializationService) {
        this.teacherRepository = teacherRepository;
        this.dayRepository = dayRepository;
        this.timeRepository = timeRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public ResponseEntity<TeacherRestrictionDto> getRestrictions(@PathVariable Long teacherId) {
        return teacherRepository.findById(teacherId)
                .map(teacher -> {
                    Restriction restriction = teacher.getRestriction();
                    List<TeacherRestrictionDto.RestrictedSlotDto> slots = restriction.getEntries().stream()
                            .filter(entry -> entry.getGrade() == Grade.TERRIBLE || entry.getGrade() == Grade.BAD)
                            .map(entry -> new TeacherRestrictionDto.RestrictedSlotDto(
                                    entry.getDay().getId(),
                                    entry.getTimeSlot().getId()))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(new TeacherRestrictionDto(teacherId, slots));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Void> updateRestrictions(@PathVariable Long teacherId, @RequestBody TeacherRestrictionDto dto) {
        return teacherRepository.findById(teacherId)
                .map(teacher -> {
                    Restriction restriction = teacher.getRestriction();
                    
                    // Clear existing entries
                    restriction.getEntries().clear();
                    
                    // Add new entries from DTO
                    if (dto.getRestrictedSlots() != null) {
                        for (TeacherRestrictionDto.RestrictedSlotDto slotDto : dto.getRestrictedSlots()) {
                            Day day = dayRepository.findById(slotDto.getDayId()).orElse(null);
                            Time time = timeRepository.findById(slotDto.getTimeId()).orElse(null);
                            
                            if (day != null && time != null) {
                                RestrictionEntryJpa entry = new RestrictionEntryJpa(restriction, day, time, Grade.TERRIBLE);
                                restriction.getEntries().add(entry);
                            }
                        }
                    }
                    
                    teacherRepository.save(teacher);
                    dataInitializationService.initializeData(); // Refresh cache
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
