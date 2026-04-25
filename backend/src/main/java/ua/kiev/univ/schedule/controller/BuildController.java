package ua.kiev.univ.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kiev.univ.schedule.service.BuildService;

@RestController
@RequestMapping("/api/build")
public class BuildController {

    private final BuildService buildService;

    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @PostMapping("/start")
    public String startBuild(@org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
                             @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate) {
        java.time.LocalDate start = (startDate != null && !startDate.isEmpty()) ? java.time.LocalDate.parse(startDate) : null;
        java.time.LocalDate end = (endDate != null && !endDate.isEmpty()) ? java.time.LocalDate.parse(endDate) : null;
        return buildService.buildSchedule(start, end);
    }

    @GetMapping("/status")
    public BuildService.BuildStatus getStatus() {
        return buildService.getStatus();
    }
}