package ua.kiev.univ.schedule.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ua.kiev.univ.schedule.dto.ScheduleEntryDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ScheduleExportService {

    public byte[] exportToExcel(String title, List<ScheduleEntryDto> schedule) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Schedule");

            // Title Row
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(2);
            String[] columns = {"Day", "Start", "End", "Subject", "Type", "Building", "Auditorium", "Teachers/Groups"};
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int rowIdx = 3;
            for (ScheduleEntryDto entry : schedule) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(entry.getDayName());
                row.createCell(1).setCellValue(entry.getTimeStart());
                row.createCell(2).setCellValue(entry.getTimeEnd());
                row.createCell(3).setCellValue(entry.getSubjectName());
                row.createCell(4).setCellValue(entry.getType());
                row.createCell(5).setCellValue(entry.getBuildingName());
                row.createCell(6).setCellValue(entry.getAuditoriumName());
                row.createCell(7).setCellValue(entry.getAdditionalInfo());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportToPdf(String title, List<ScheduleEntryDto> schedule) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2, 5, 3, 3, 3, 5});

            String[] columns = {"Day", "Start", "End", "Subject", "Type", "Building", "Room", "Info"};
            for (String col : columns) {
                PdfPCell cell = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (ScheduleEntryDto entry : schedule) {
                table.addCell(new Phrase(entry.getDayName() != null ? entry.getDayName() : ""));
                table.addCell(new Phrase(entry.getTimeStart() != null ? entry.getTimeStart() : ""));
                table.addCell(new Phrase(entry.getTimeEnd() != null ? entry.getTimeEnd() : ""));
                table.addCell(new Phrase(entry.getSubjectName() != null ? entry.getSubjectName() : ""));
                table.addCell(new Phrase(entry.getType() != null ? entry.getType() : ""));
                table.addCell(new Phrase(entry.getBuildingName() != null ? entry.getBuildingName() : ""));
                table.addCell(new Phrase(entry.getAuditoriumName() != null ? entry.getAuditoriumName() : ""));
                table.addCell(new Phrase(entry.getAdditionalInfo() != null ? entry.getAdditionalInfo() : ""));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }
}
