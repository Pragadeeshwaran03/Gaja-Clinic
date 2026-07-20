package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.WhatsAppShareInfo;
import com.gaja.clinic.dto.WhatsAppShareResult;
import com.gaja.clinic.service.AppUrlService;
import com.gaja.clinic.service.WhatsAppShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class WhatsAppShareServiceImpl implements WhatsAppShareService {

    private static final Pattern NON_DIGITS = Pattern.compile("[^\\d]");

    private final AppUrlService appUrlService;

    @Override
    public WhatsAppShareResult buildShareUrl(WhatsAppShareInfo info) {
        if (info.getPatientName() == null || info.getPatientName().isBlank()) {
            return WhatsAppShareResult.fail("Patient name is not available.");
        }
        if (info.getBillNo() == null || info.getBillNo().isBlank()) {
            return WhatsAppShareResult.fail("Bill number is not available.");
        }
        if (info.getPrescriptionNo() == null || info.getPrescriptionNo().isBlank()) {
            return WhatsAppShareResult.fail("Prescription number is not available.");
        }
        if (info.getPdfWebPath() == null || info.getPdfWebPath().isBlank()) {
            return WhatsAppShareResult.fail("PDF is not available yet. Please download the PDF first or try again.");
        }

        String mobile = normalizeMobile(info.getPatientMobile());
        if (mobile == null) {
            return WhatsAppShareResult.fail(
                    "Patient mobile number is invalid. Enter a valid 10-digit Indian mobile number.");
        }

        String pdfLink = info.getShareUrl() != null && !info.getShareUrl().isBlank()
                ? info.getShareUrl()
                : appUrlService.getBillShareUrl(info.getBillId());

        String message = buildMessage(
                info.getPatientName(),
                info.getBillNo(),
                info.getPrescriptionNo(),
                info.getFinalAmount(),
                pdfLink);

        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return WhatsAppShareResult.ok("https://wa.me/" + mobile + "?text=" + encoded);
    }

    private static String normalizeMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            return null;
        }
        String digits = NON_DIGITS.matcher(mobile).replaceAll("");
        if (digits.startsWith("91") && digits.length() == 12) {
            return digits;
        }
        if (digits.length() == 10 && digits.charAt(0) >= '6' && digits.charAt(0) <= '9') {
            return "91" + digits;
        }
        return null;
    }

    private static String buildMessage(String patientName, String billNo, String prescriptionNo,
                                       BigDecimal finalAmount, String pdfUrl) {
        String amount = finalAmount != null ? String.format("%.2f", finalAmount) : "0.00";
        return String.join("\n",
                "Hello " + patientName + ",",
                "Your prescription and bill are ready.",
                "",
                "Bill No: " + billNo,
                "Prescription No: " + prescriptionNo,
                "Total Amount: ₹" + amount,
                "",
                pdfUrl,
                "",
                "Thank you,",
                "Gaja Clinic & Medicals");
    }
}
