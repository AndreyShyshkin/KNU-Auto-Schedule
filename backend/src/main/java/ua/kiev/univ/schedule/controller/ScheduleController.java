package ua.kiev.univ.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.ScheduleEntryDto;
import ua.kiev.univ.schedule.service.ScheduleQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleQueryService scheduleQueryService;

    @DeleteMapping("/clear")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> clearSchedule() {
        // Clear ONLY generated results tables
        String[] tables = {
            "appointment_entry", "appointment"
        };
        for (String table : tables) {
            try {
                jdbcTemplate.execute("TRUNCATE TABLE " + table + " CASCADE");
            } catch (Exception e) {
                System.err.println("Could not truncate " + table + ": " + e.getMessage());
            }
        }
        
        dataInitializationService.initializeData();
        return ResponseEntity.ok().build();
    }

    private final ua.kiev.univ.schedule.repository.AppointmentRepository appointmentRepository;
    private final ua.kiev.univ.schedule.service.core.DataInitializationService dataInitializationService;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public ScheduleController(ScheduleQueryService scheduleQueryService, 
                              ua.kiev.univ.schedule.repository.AppointmentRepository appointmentRepository, 
                              ua.kiev.univ.schedule.service.core.DataInitializationService dataInitializationService,
                              org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.scheduleQueryService = scheduleQueryService;
        this.appointmentRepository = appointmentRepository;
        this.dataInitializationService = dataInitializationService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/teacher/{id}")
    public List<ScheduleEntryDto> getTeacherSchedule(@PathVariable Long id) {
        return scheduleQueryService.getTeacherSchedule(id);
    }

    @GetMapping("/group/{id}")
    public List<ScheduleEntryDto> getGroupSchedule(@PathVariable Long id) {
        return scheduleQueryService.getGroupSchedule(id);
    }
}