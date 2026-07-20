package com.gaja.clinic.controller.admin;

import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/main-admin/reports")
@PreAuthorize("hasRole('MainAdmin')")
@RequiredArgsConstructor
public class ReportsController {

    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ExcelExportService excelExportService;

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/reports";
    }

    @GetMapping("/export-patients")
    public ResponseEntity<byte[]> exportPatients() {
        return excelResponse(excelExportService.exportPatients(), "Patients_" + FILE_DATE.format(LocalDate.now()) + ".xlsx");
    }

    @GetMapping("/export-bills")
    public ResponseEntity<byte[]> exportBills() {
        return excelResponse(excelExportService.exportBills(), "Bills_" + FILE_DATE.format(LocalDate.now()) + ".xlsx");
    }

    @GetMapping("/export-pdf-archive")
    public ResponseEntity<byte[]> exportPdfArchive() {
        return excelResponse(excelExportService.exportPdfArchive(),
                "PdfArchive_" + FILE_DATE.format(LocalDate.now()) + ".xlsx");
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
