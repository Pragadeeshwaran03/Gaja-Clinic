package com.gaja.clinic.service.impl;

import com.gaja.clinic.entity.Doctor;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.entity.PrescriptionStatus;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.DoctorRepository;
import com.gaja.clinic.repository.PatientRepository;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.ConsultationService;
import com.gaja.clinic.service.IdGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final IdGenerationService idGenerationService;

    @Override
    @Transactional
    public Prescription startConsultation(Integer patientId, Integer doctorUserId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber(idGenerationService.generatePrescriptionNumber());
        prescription.setPatientId(patientId);
        prescription.setDoctorId(doctor.getId());
        prescription.setDateCreated(LocalDateTime.now(ZoneOffset.UTC));
        prescription.setStatus(PrescriptionStatus.PENDING_PHARMACY);
        return prescriptionRepository.save(prescription);
    }
}
