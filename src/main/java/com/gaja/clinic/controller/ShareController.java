package com.gaja.clinic.controller;

import com.gaja.clinic.entity.PdfRecord;
import com.gaja.clinic.repository.PdfRecordRepository;
import com.gaja.clinic.service.PdfStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Files;

@Controller
@RequiredArgsConstructor
public class ShareController {

    private final PdfRecordRepository pdfRecordRepository;
    private final PdfStorageService pdfStorageService;

    @GetMapping("/share/pdf/{billId}")
    public String pdf(@PathVariable Integer billId) {
        PdfRecord record = pdfRecordRepository.findByBillId(billId).orElse(null);
        if (record == null || record.getFilePath() == null || record.getFilePath().isBlank()) {
            return "redirect:/account/login";
        }
        var path = pdfStorageService.resolvePhysicalPath(record.getFilePath());
        if (!Files.exists(path)) {
            return "redirect:/account/login";
        }
        return "redirect:" + record.getFilePath();
    }
}
