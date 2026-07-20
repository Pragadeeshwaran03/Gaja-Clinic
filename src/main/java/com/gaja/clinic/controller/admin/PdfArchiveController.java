package com.gaja.clinic.controller.admin;

import com.gaja.clinic.dto.PdfArchiveFilterDto;
import com.gaja.clinic.entity.PdfRecord;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.EmailService;
import com.gaja.clinic.service.PdfArchiveService;
import com.gaja.clinic.service.PdfStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/main-admin/pdf-archive")
@PreAuthorize("hasRole('MainAdmin')")
@RequiredArgsConstructor
public class PdfArchiveController {

    private static final DateTimeFormatter VISIT_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final PdfArchiveService pdfArchiveService;
    private final PdfStorageService pdfStorageService;
    private final EmailService emailService;

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails user,
                        @ModelAttribute PdfArchiveFilterDto filter,
                        Model model) {
        filter.setRecords(pdfArchiveService.search(filter));
        model.addAttribute("user", user);
        model.addAttribute("filter", filter);
        return "admin/pdf-archive";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam Integer id) throws Exception {
        PdfRecord record = pdfArchiveService.getById(id);
        Path filePath = pdfStorageService.resolvePhysicalPath(record.getFilePath());
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = Files.readAllBytes(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + record.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(bytes));
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam Integer id,
                            @RequestParam String toEmail,
                            RedirectAttributes redirectAttributes) {
        try {
            PdfRecord record = pdfArchiveService.getById(id);
            Path filePath = pdfStorageService.resolvePhysicalPath(record.getFilePath());
            if (!Files.exists(filePath)) {
                redirectAttributes.addFlashAttribute("error", "PDF file not found on disk.");
                return "redirect:/main-admin/pdf-archive";
            }

            String patientName = record.getPatient() != null ? record.getPatient().getName() : "Patient";
            String subject = String.format("Prescription - %s (%s)",
                    patientName, VISIT_DATE.format(record.getVisitDate()));
            String body = String.format(
                    "<p>Dear %s,</p><p>Please find your prescription attached.</p><p>— Gaja Clinic & Medicals</p>",
                    patientName);
            emailService.sendEmail(toEmail, subject, body, filePath.toString());
            redirectAttributes.addFlashAttribute("success", "Email sent to " + toEmail + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Email failed: " + ex.getMessage());
        }
        return "redirect:/main-admin/pdf-archive";
    }
}
