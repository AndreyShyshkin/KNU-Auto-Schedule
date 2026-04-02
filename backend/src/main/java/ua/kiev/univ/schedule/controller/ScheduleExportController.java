package ua.kiev.univ.schedule.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.kiev.univ.schedule.dto.ScheduleEntryDto;
import ua.kiev.univ.schedule.service.ScheduleExportService;
import ua.kiev.univ.schedule.service.ScheduleQueryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/schedule/export")
public class ScheduleExportController {

    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleExportService scheduleExportService;

    public ScheduleExportController(ScheduleQueryService scheduleQueryService, ScheduleExportService scheduleExportService) {
        this.scheduleQueryService = scheduleQueryService;
        this.scheduleExportService = scheduleExportService;
    }

    @PostMapping("/excel")
    public void exportExcel(@RequestBody List<ScheduleEntryDto> data, @RequestParam(defaultValue = "Schedule") String title, HttpServletResponse response) throws IOException {
        byte[] bytes = scheduleExportService.exportToExcel(title, data);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"schedule.xlsx\"");
        response.getOutputStream().write(bytes);
    }

    @PostMapping("/pdf")
    public void exportPdf(@RequestBody List<ScheduleEntryDto> data, @RequestParam(defaultValue = "Schedule") String title, HttpServletResponse response) throws IOException {
        byte[] bytes = scheduleExportService.exportToPdf(title, data);
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"schedule.pdf\"");
        response.getOutputStream().write(bytes);
    }
}
