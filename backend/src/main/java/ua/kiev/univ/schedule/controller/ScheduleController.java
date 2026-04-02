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
    public ResponseEntity<Void> clearSchedule(@RequestParam(required = false) Long versionId) {
        if (versionId != null) {
            jdbcTemplate.update("DELETE FROM appointment_entry WHERE appointment_id IN (SELECT id FROM appointment WHERE version_id = ?)", versionId);
            jdbcTemplate.update("DELETE FROM appointment WHERE version_id = ?", versionId);
            jdbcTemplate.update("DELETE FROM schedule_version WHERE id = ?", versionId);
        } else {
            String[] tables = {
                "appointment_entry", "appointment", "schedule_version"
            };
            for (String table : tables) {
                try {
                    jdbcTemplate.execute("TRUNCATE TABLE " + table + " CASCADE");
                } catch (Exception e) {
                    System.err.println("Could not truncate " + table + ": " + e.getMessage());
                }
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
    public List<ScheduleEntryDto> getTeacherSchedule(@PathVariable Long id, @RequestParam(required = false) Long versionId) {
        return scheduleQueryService.getTeacherSchedule(id, versionId);
    }

    @GetMapping("/group/{id}")
    public List<ScheduleEntryDto> getGroupSchedule(@PathVariable Long id, @RequestParam(required = false) Long versionId) {
        return scheduleQueryService.getGroupSchedule(id, versionId);
    }

    @GetMapping("/all")
    public List<ScheduleEntryDto> getAllSchedule(@RequestParam(required = false) Long versionId) {
        return scheduleQueryService.getAllSchedule(versionId);
    }
}