package com.gaja.clinic.service;

public interface IdGenerationService {

    String generatePatientId();

    String generatePrescriptionNumber();

    String generateBillNo();
}
