package ua.kiev.univ.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kiev.univ.schedule.dto.ScheduleEntryDto;
import ua.kiev.univ.schedule.service.ScheduleQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleQueryService scheduleQueryService;

    public ScheduleController(ScheduleQueryService scheduleQueryService) {
        this.scheduleQueryService = scheduleQueryService;
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