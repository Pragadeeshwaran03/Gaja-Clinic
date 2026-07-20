package com.gaja.clinic.service;

import com.gaja.clinic.entity.Bill;
import com.gaja.clinic.entity.Prescription;

public interface PdfGenerationService {

    byte[] generatePrescriptionPdf(Prescription prescription, Bill bill);
}
