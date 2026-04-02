package ua.kiev.univ.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kiev.univ.schedule.model.appointment.ScheduleVersion;
import ua.kiev.univ.schedule.repository.ScheduleVersionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/schedule/versions")
public class ScheduleVersionController {

    private final ScheduleVersionRepository scheduleVersionRepository;

    public ScheduleVersionController(ScheduleVersionRepository scheduleVersionRepository) {
        this.scheduleVersionRepository = scheduleVersionRepository;
    }

    @GetMapping
    public List<ScheduleVersion> getVersions() {
        return scheduleVersionRepository.findByOrderByCreatedAtDesc();
    }
}
