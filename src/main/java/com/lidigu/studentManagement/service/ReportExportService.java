package com.lidigu.studentManagement.service;

import com.lidigu.studentManagement.entity.BullyingReport;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportExportService {

    // ----------------------------------------------------------------
    // Excel Export
    // ----------------------------------------------------------------

    public byte[] exportToExcel(List<BullyingReport> reports) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Bullying Reports");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header row
            String[] columns = {"ID", "Reported By", "Date Reported", "Incident Date", "Location", "Status", "Proposed Status", "Anonymous", "Description"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4500);
            }
            sheet.setColumnWidth(8, 10000); // Description wider

            // Data rows
            int rowNum = 1;
            for (BullyingReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getId() != null ? report.getId() : 0);
                row.createCell(1).setCellValue(report.isAnonymous() ? "Anonymous"
                        : (report.getReportedBy() != null ? report.getReportedBy().getUserName() : "Unknown"));
                row.createCell(2).setCellValue(report.getCreatedAt() != null ? report.getCreatedAt().toString() : "");
                row.createCell(3).setCellValue(report.getIncidentDate() != null ? report.getIncidentDate().toString() : "");
                row.createCell(4).setCellValue(report.getLocation() != null ? report.getLocation() : "");
                row.createCell(5).setCellValue(report.getStatus() != null ? report.getStatus() : "");
                row.createCell(6).setCellValue(report.getProposedStatus() != null ? report.getProposedStatus() : "");
                row.createCell(7).setCellValue(report.isAnonymous() ? "Yes" : "No");
                row.createCell(8).setCellValue(report.getDescription() != null ? report.getDescription() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel export", e);
        }
    }

    // ----------------------------------------------------------------
    // PDF Export
    // ----------------------------------------------------------------

    public byte[] exportToPdf(List<BullyingReport> reports) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate()); // landscape for wider table
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Bullying Reports", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15f);
            document.add(title);

            // Table with 7 columns
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 2.5f, 2.5f, 2f, 2f, 4f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            String[] headers = {"ID", "Reported By", "Date Reported", "Incident Date", "Location", "Status", "Description"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(0, 51, 102));
                cell.setPadding(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            for (BullyingReport report : reports) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(report.getId()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(
                        report.isAnonymous() ? "Anonymous"
                                : (report.getReportedBy() != null ? report.getReportedBy().getUserName() : "Unknown"),
                        dataFont)));
                table.addCell(new PdfPCell(new Phrase(report.getCreatedAt() != null ? report.getCreatedAt().toString() : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(report.getIncidentDate() != null ? report.getIncidentDate().toString() : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(report.getLocation() != null ? report.getLocation() : "", dataFont)));

                // Colour-code status cell
                PdfPCell statusCell = new PdfPCell(new Phrase(report.getStatus() != null ? report.getStatus() : "", dataFont));
                if ("PENDING".equals(report.getStatus())) statusCell.setBackgroundColor(new Color(255, 243, 205));
                else if ("UNDER_REVIEW".equals(report.getStatus())) statusCell.setBackgroundColor(new Color(204, 229, 255));
                else if ("RESOLVED".equals(report.getStatus())) statusCell.setBackgroundColor(new Color(212, 237, 218));
                else if ("TEACHER_ACTION_PENDING".equals(report.getStatus())) statusCell.setBackgroundColor(new Color(255, 220, 150));
                table.addCell(statusCell);

                table.addCell(new PdfPCell(new Phrase(report.getDescription() != null ? report.getDescription() : "", dataFont)));
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF export", e);
        }
    }
}
