package ua.kiev.univ.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.kiev.univ.schedule.service.DataExchangeService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@Tag(name = "Data Exchange", description = "Endpoints for importing and exporting data as ZIP")
public class DataExchangeController {

    private final DataExchangeService dataExchangeService;

    public DataExchangeController(DataExchangeService dataExchangeService) {
        this.dataExchangeService = dataExchangeService;
    }

    @GetMapping("/export/available")
    @Operation(summary = "Get list of tables available for export")
    public List<String> getAvailableTables() {
        return dataExchangeService.getAvailableTables();
    }

    @PostMapping(value = "/export", produces = "application/zip")
    @Operation(summary = "Export selected tables as ZIP archive")
    public void exportData(@RequestBody List<String> tables, HttpServletResponse response) throws IOException {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"schedule_data.zip\"");
        dataExchangeService.exportToZip(tables, response.getOutputStream());
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import data from ZIP archive")
    public Map<String, String> importData(@RequestParam("file") MultipartFile file, @RequestParam("tables") List<String> tables) throws IOException {
        return dataExchangeService.importFromZip(file.getInputStream(), tables);
    }
}
