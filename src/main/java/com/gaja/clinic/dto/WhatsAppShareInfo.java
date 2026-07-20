package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WhatsAppShareInfo {

    private final Integer billId;
    private final String patientName;
    private final String patientMobile;
    private final String billNo;
    private final String prescriptionNo;
    private final BigDecimal finalAmount;
    private final String pdfWebPath;
    private final String shareUrl;
}
