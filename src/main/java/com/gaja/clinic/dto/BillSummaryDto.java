package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class BillSummaryDto {

    private final Integer billId;
    private final String billNo;
    private final String prescriptionNumber;
    private final String patientName;
    private final String patientMobile;
    private final String doctorName;
    private final BigDecimal consultationFee;
    private final BigDecimal otherCharges;
    private final String otherChargesDescription;
    private final BigDecimal discount;
    private final BigDecimal finalAmount;
    private final PaymentDto payment;
    private final String pdfFilePath;
    private final String whatsAppShareUrl;
    private final String whatsAppShareError;
    private final LocalDateTime createdDate;
}
