package ua.kiev.univ.schedule.controller;

import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.model.appointment.ScheduleVersion;
import ua.kiev.univ.schedule.repository.ScheduleVersionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-versions")
public class ScheduleVersionController {

    private final ScheduleVersionRepository scheduleVersionRepository;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    private final ua.kiev.univ.schedule.service.core.DataInitializationService dataInitializationService;

    public ScheduleVersionController(ScheduleVersionRepository scheduleVersionRepository,
                                     org.springframework.jdbc.core.JdbcTemplate jdbcTemplate,
                                     ua.kiev.univ.schedule.service.core.DataInitializationService dataInitializationService) {
        this.scheduleVersionRepository = scheduleVersionRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.dataInitializationService = dataInitializationService;
    }

    @GetMapping
    public List<ScheduleVersion> getVersions() {
        return scheduleVersionRepository.findByOrderByCreatedAtDesc();
    }

    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public void deleteVersion(@PathVariable Long id) {
        // Delete entries first (FK constraint)
        jdbcTemplate.update("DELETE FROM appointment_entry WHERE appointment_id IN (SELECT id FROM appointment WHERE version_id = ?)", id);
        // Delete appointments
        jdbcTemplate.update("DELETE FROM appointment WHERE version_id = ?", id);
        // Finally delete the version
        scheduleVersionRepository.deleteById(id);
        dataInitializationService.initializeData();
    }
}
