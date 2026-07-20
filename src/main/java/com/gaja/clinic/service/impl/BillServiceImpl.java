package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.*;
import com.gaja.clinic.entity.*;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.BillRepository;
import com.gaja.clinic.repository.PdfRecordRepository;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillServiceImpl implements BillService {

    private final PrescriptionRepository prescriptionRepository;
    private final BillRepository billRepository;
    private final PdfRecordRepository pdfRecordRepository;
    private final IdGenerationService idGenerationService;
    private final SettingsService settingsService;
    private final PaymentService paymentService;
    private final PdfGenerationService pdfGenerationService;
    private final PdfStorageService pdfStorageService;
    private final WhatsAppShareService whatsAppShareService;
    private final AppUrlService appUrlService;

    @Override
    @Transactional(readOnly = true)
    public BillCreateDto buildBillForm(Integer prescriptionId) {
        prescriptionRepository
                .findPendingDetailsById(prescriptionId, PrescriptionStatus.PENDING_PHARMACY)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found or already billed"));

        BillCreateDto dto = new BillCreateDto();
        dto.setPrescriptionId(prescriptionId);
        dto.setConsultationFee(settingsService.getSettings().getConsultationFeeDefault());
        dto.setOtherCharges(BigDecimal.ZERO);
        dto.setOtherChargesDescription("");
        dto.setDiscount(BigDecimal.ZERO);
        dto.setPaymentMethod("Cash");
        return dto;
    }

    @Override
    @Transactional
    public BillSaveResponse processBill(BillCreateDto dto, String staffName) {
        if (dto.getConsultationFee() == null || dto.getConsultationFee().compareTo(BigDecimal.ZERO) < 0) {
            return BillSaveResponse.error("Consultation fee cannot be negative.");
        }
        if (dto.getOtherCharges() == null || dto.getOtherCharges().compareTo(BigDecimal.ZERO) < 0) {
            return BillSaveResponse.error("Other charges cannot be negative.");
        }
        if (dto.getDiscount() == null || dto.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            return BillSaveResponse.error("Discount cannot be negative.");
        }
        if ("UPI".equals(dto.getPaymentMethod())
                && (dto.getUtrNo() == null || dto.getUtrNo().isBlank())) {
            return BillSaveResponse.error("UTR number is required for UPI payments.");
        }
        if (!"UPI".equals(dto.getPaymentMethod())) {
            dto.setUtrNo(null);
        }

        BigDecimal subtotal = dto.getConsultationFee().add(dto.getOtherCharges());
        BigDecimal finalAmount = subtotal.subtract(dto.getDiscount());
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            return BillSaveResponse.error("Discount cannot exceed the total bill amount.");
        }

        Prescription prescription = prescriptionRepository
                .findByIdAndStatusWithItems(dto.getPrescriptionId(), PrescriptionStatus.PENDING_PHARMACY)
                .orElse(null);
        if (prescription == null) {
            return BillSaveResponse.error("Prescription not found or already billed.");
        }

        Bill bill = new Bill();
        bill.setBillNo(idGenerationService.generateBillNo());
        bill.setPrescriptionId(prescription.getId());
        bill.setPatientId(prescription.getPatientId());
        bill.setDoctorId(prescription.getDoctorId());
        bill.setConsultationFee(dto.getConsultationFee());
        bill.setOtherCharges(dto.getOtherCharges());
        bill.setOtherChargesDescription(trimOrBlank(dto.getOtherChargesDescription()));
        bill.setDiscount(dto.getDiscount());
        bill.setFinalAmount(finalAmount);
        bill.setStaffName(staffName);
        bill.setCreatedDate(LocalDateTime.now());
        bill = billRepository.save(bill);

        paymentService.createPaidPayment(bill.getId(), dto.getPaymentMethod(), dto.getUtrNo(), staffName);

        prescription.setStatus(PrescriptionStatus.COMPLETED);
        prescriptionRepository.save(prescription);

        log.info("Bill {} created, amount {}", bill.getBillNo(), bill.getFinalAmount());

        tryGenerateAndStorePdf(bill.getId());

        return BillSaveResponse.ok("/pharmacy/bill/success?billId=" + bill.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BillSummaryDto getBillSummary(Integer billId) {
        Bill bill = billRepository.findByIdWithDetails(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        Payment payment = bill.getPayment();

        String doctorName = bill.getDoctor() != null && bill.getDoctor().getUser() != null
                ? bill.getDoctor().getUser().getFullName()
                : "—";

        String pdfPath = bill.getPdfRecord() != null ? bill.getPdfRecord().getFilePath() : null;
        WhatsAppShareResult shareResult = whatsAppShareService.buildShareUrl(WhatsAppShareInfo.builder()
                .billId(bill.getId())
                .patientName(bill.getPatient() != null ? bill.getPatient().getName() : "")
                .patientMobile(bill.getPatient() != null ? bill.getPatient().getMobile() : null)
                .billNo(bill.getBillNo())
                .prescriptionNo(bill.getPrescription() != null ? bill.getPrescription().getPrescriptionNumber() : "")
                .finalAmount(bill.getFinalAmount())
                .pdfWebPath(pdfPath)
                .shareUrl(appUrlService.getBillShareUrl(bill.getId()))
                .build());

        return BillSummaryDto.builder()
                .billId(bill.getId())
                .billNo(bill.getBillNo())
                .prescriptionNumber(bill.getPrescription() != null ? bill.getPrescription().getPrescriptionNumber() : "")
                .patientName(bill.getPatient() != null ? bill.getPatient().getName() : "")
                .patientMobile(bill.getPatient() != null ? bill.getPatient().getMobile() : "")
                .doctorName(doctorName)
                .consultationFee(bill.getConsultationFee())
                .otherCharges(bill.getOtherCharges())
                .otherChargesDescription(bill.getOtherChargesDescription())
                .discount(bill.getDiscount())
                .finalAmount(bill.getFinalAmount())
                .payment(paymentService.toDto(payment))
                .pdfFilePath(pdfPath)
                .whatsAppShareUrl(shareResult.isSuccess() ? shareResult.getShareUrl() : null)
                .whatsAppShareError(shareResult.isSuccess() ? null : shareResult.getErrorMessage())
                .createdDate(bill.getCreatedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Bill getBillForDownload(Integer billId) {
        return billRepository.findByIdWithDetails(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
    }

    @Override
    @Transactional
    public void tryGenerateAndStorePdf(Integer billId) {
        try {
            Bill bill = billRepository.findByIdWithDetails(billId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

            Prescription prescription = prescriptionRepository.findByIdWithFullDetails(bill.getPrescriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

            byte[] pdfBytes = pdfGenerationService.generatePrescriptionPdf(prescription, bill);
            String webPath = pdfStorageService.storePdf(pdfBytes, prescription.getPrescriptionNumber());
            String fileName = webPath.substring(webPath.lastIndexOf('/') + 1);

            PdfRecord record = pdfRecordRepository.findByBillId(billId).orElse(null);
            if (record == null) {
                record = new PdfRecord();
                record.setPatientId(prescription.getPatientId());
                record.setDoctorId(prescription.getDoctorId());
                record.setBillId(bill.getId());
                record.setPrescriptionId(prescription.getId());
                record.setVisitDate(prescription.getDateCreated());
                record.setCreatedDate(LocalDateTime.now());
            }
            record.setFileName(fileName);
            record.setFilePath(webPath);
            pdfRecordRepository.save(record);

            log.info("Bill {} — PDF saved at {}", bill.getBillNo(), webPath);
        } catch (Exception ex) {
            log.error("PDF generation failed for bill {}", billId, ex);
        }
    }

    private static String trimOrBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
