package com.gaja.clinic.service.impl;

import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.entity.PrescriptionStatus;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.PharmacyPrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyPrescriptionServiceImpl implements PharmacyPrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> listPendingPrescriptions() {
        return prescriptionRepository.findByStatusWithPatientAndDoctorOrderByDateCreatedDesc(
                PrescriptionStatus.PENDING_PHARMACY);
    }

    @Override
    @Transactional(readOnly = true)
    public Prescription getPendingPrescriptionDetails(Integer prescriptionId) {
        return prescriptionRepository.findPendingDetailsById(prescriptionId, PrescriptionStatus.PENDING_PHARMACY)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found or already billed"));
    }
}
