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
    public String startBuild() {
        return buildService.buildSchedule();
    }

    @GetMapping("/status")
    public boolean getStatus() {
        return buildService.isBuilding();
    }
}