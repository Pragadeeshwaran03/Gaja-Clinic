package com.gaja.clinic.controller.pharmacy;

import com.gaja.clinic.dto.BillCreateDto;
import com.gaja.clinic.dto.BillSaveResponse;
import com.gaja.clinic.dto.BillSummaryDto;
import com.gaja.clinic.entity.Bill;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.security.CustomUserDetails;
import com.gaja.clinic.service.BillService;
import com.gaja.clinic.service.PharmacyPrescriptionService;
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

import java.nio.file.Files;

@Controller
@RequestMapping("/pharmacy/bill")
@PreAuthorize("hasRole('Pharmacy')")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;
    private final PdfStorageService pdfStorageService;
    private final PharmacyPrescriptionService pharmacyPrescriptionService;

    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal CustomUserDetails user,
                             @RequestParam Integer prescriptionId,
                             Model model) {
        BillCreateDto dto = billService.buildBillForm(prescriptionId);
        Prescription prescription = pharmacyPrescriptionService.getPendingPrescriptionDetails(prescriptionId);
        model.addAttribute("user", user);
        model.addAttribute("billCreateDto", dto);
        model.addAttribute("prescription", prescription);
        return "pharmacy/bill-create";
    }

    @PostMapping("/create")
    @ResponseBody
    public BillSaveResponse create(@AuthenticationPrincipal CustomUserDetails user,
                                   @ModelAttribute BillCreateDto dto) {
        try {
            String staffName = user != null ? user.getDisplayName() : null;
            return billService.processBill(dto, staffName);
        } catch (Exception ex) {
            return BillSaveResponse.error("Billing failed: " + ex.getMessage());
        }
    }

    @GetMapping("/success")
    public String success(@AuthenticationPrincipal CustomUserDetails user,
                          @RequestParam Integer billId,
                          Model model) {
        BillSummaryDto summary = billService.getBillSummary(billId);
        model.addAttribute("user", user);
        model.addAttribute("summary", summary);
        return "pharmacy/bill-success";
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<Resource> downloadPdf(@RequestParam Integer billId) throws Exception {
        Bill bill = billService.getBillForDownload(billId);

        if (bill.getPdfRecord() != null) {
            var path = pdfStorageService.resolvePhysicalPath(bill.getPdfRecord().getFilePath());
            if (Files.exists(path)) {
                byte[] bytes = Files.readAllBytes(path);
                return pdfResponse(bytes, bill.getPdfRecord().getFileName());
            }
        }

        billService.tryGenerateAndStorePdf(billId);
        bill = billService.getBillForDownload(billId);
        if (bill.getPdfRecord() == null) {
            return ResponseEntity.badRequest().build();
        }

        var path = pdfStorageService.resolvePhysicalPath(bill.getPdfRecord().getFilePath());
        byte[] bytes = Files.readAllBytes(path);
        return pdfResponse(bytes, bill.getPdfRecord().getFileName());
    }

    @GetMapping("/share-whatsapp")
    public String shareWhatsApp(@RequestParam Integer billId) {
        BillSummaryDto summary = billService.getBillSummary(billId);
        if (summary.getWhatsAppShareUrl() == null || summary.getWhatsAppShareUrl().isBlank()) {
            return "redirect:/pharmacy/bill/success?billId=" + billId;
        }
        return "redirect:" + summary.getWhatsAppShareUrl();
    }

    private ResponseEntity<Resource> pdfResponse(byte[] bytes, String fileName) {
        ByteArrayResource resource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
