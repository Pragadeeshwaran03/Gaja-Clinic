package com.gaja.clinic.service;

import com.gaja.clinic.entity.Prescription;

import java.util.List;

public interface PharmacyPrescriptionService {

    List<Prescription> listPendingPrescriptions();

    Prescription getPendingPrescriptionDetails(Integer prescriptionId);
}
