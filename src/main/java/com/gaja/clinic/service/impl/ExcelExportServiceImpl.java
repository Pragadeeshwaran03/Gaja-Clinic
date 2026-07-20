package com.gaja.clinic.service.impl;

import com.gaja.clinic.entity.Bill;
import com.gaja.clinic.entity.Patient;
import com.gaja.clinic.entity.PdfRecord;
import com.gaja.clinic.repository.BillRepository;
import com.gaja.clinic.repository.PatientRepository;
import com.gaja.clinic.repository.PdfRecordRepository;
import com.gaja.clinic.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

    private final PatientRepository patientRepository;
    private final BillRepository billRepository;
    private final PdfRecordRepository pdfRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPatients() {
        List<Patient> patients = patientRepository.findAll();
        patients.sort((a, b) -> a.getPatientCode().compareTo(b.getPatientCode()));

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet ws = wb.createSheet("Patients");
            Row header = ws.createRow(0);
            header.createCell(0).setCellValue("Patient Code");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Mobile");
            header.createCell(3).setCellValue("Age");
            header.createCell(4).setCellValue("Gender");
            header.createCell(5).setCellValue("Registered");

            int rowNum = 1;
            for (Patient p : patients) {
                Row row = ws.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getPatientCode());
                row.createCell(1).setCellValue(p.getName());
                row.createCell(2).setCellValue(p.getMobile());
                row.createCell(3).setCellValue(p.getAge() != null ? p.getAge() : 0);
                row.createCell(4).setCellValue(p.getGender() != null ? p.getGender() : "");
                row.createCell(5).setCellValue(p.getCreatedDate() != null ? p.getCreatedDate().toString() : "");
            }
            for (int i = 0; i < 6; i++) {
                ws.autoSizeColumn(i);
            }
            return workbookToBytes(wb);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to export patients", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportBills() {
        List<Bill> bills = billRepository.findAll();
        bills.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet ws = wb.createSheet("Bills");
            Row header = ws.createRow(0);
            header.createCell(0).setCellValue("Bill No");
            header.createCell(1).setCellValue("Patient");
            header.createCell(2).setCellValue("Consultation");
            header.createCell(3).setCellValue("Other Charges");
            header.createCell(4).setCellValue("Discount");
            header.createCell(5).setCellValue("Final Amount");
            header.createCell(6).setCellValue("Payment");
            header.createCell(7).setCellValue("Date");

            int rowNum = 1;
            for (Bill b : bills) {
                Row row = ws.createRow(rowNum++);
                row.createCell(0).setCellValue(b.getBillNo());
                row.createCell(1).setCellValue(b.getPatient() != null ? b.getPatient().getName() : "");
                row.createCell(2).setCellValue(b.getConsultationFee().doubleValue());
                row.createCell(3).setCellValue(b.getOtherCharges() != null ? b.getOtherCharges().doubleValue() : 0);
                row.createCell(4).setCellValue(b.getDiscount().doubleValue());
                row.createCell(5).setCellValue(b.getFinalAmount().doubleValue());
                row.createCell(6).setCellValue(b.getPayment() != null ? b.getPayment().getPaymentMethod() : "");
                row.createCell(7).setCellValue(b.getCreatedDate() != null ? b.getCreatedDate().toString() : "");
            }
            for (int i = 0; i < 8; i++) {
                ws.autoSizeColumn(i);
            }
            return workbookToBytes(wb);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to export bills", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdfArchive() {
        List<PdfRecord> records = pdfRecordRepository.findAllByOrderByCreatedDateDesc();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet ws = wb.createSheet("PDF Archive");
            Row header = ws.createRow(0);
            header.createCell(0).setCellValue("File Name");
            header.createCell(1).setCellValue("Patient");
            header.createCell(2).setCellValue("Visit Date");
            header.createCell(3).setCellValue("File Path");
            header.createCell(4).setCellValue("Created");

            int rowNum = 1;
            for (PdfRecord r : records) {
                Row row = ws.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getFileName());
                row.createCell(1).setCellValue(r.getPatient() != null ? r.getPatient().getName() : "");
                row.createCell(2).setCellValue(r.getVisitDate() != null ? r.getVisitDate().toString() : "");
                row.createCell(3).setCellValue(r.getFilePath());
                row.createCell(4).setCellValue(r.getCreatedDate() != null ? r.getCreatedDate().toString() : "");
            }
            for (int i = 0; i < 5; i++) {
                ws.autoSizeColumn(i);
            }
            return workbookToBytes(wb);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to export PDF archive", ex);
        }
    }

    private static byte[] workbookToBytes(Workbook wb) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            wb.write(out);
            return out.toByteArray();
        }
    }
}
