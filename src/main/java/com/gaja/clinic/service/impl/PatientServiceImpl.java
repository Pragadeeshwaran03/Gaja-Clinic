package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.PatientCreateDto;
import com.gaja.clinic.dto.PatientHistoryDto;
import com.gaja.clinic.dto.PatientSearchResultDto;
import com.gaja.clinic.entity.Patient;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.exception.ResourceNotFoundException;
import com.gaja.clinic.repository.PatientRepository;
import com.gaja.clinic.repository.PdfRecordRepository;
import com.gaja.clinic.repository.PrescriptionRepository;
import com.gaja.clinic.service.IdGenerationService;
import com.gaja.clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PdfRecordRepository pdfRecordRepository;
    private final IdGenerationService idGenerationService;

    @Override
    @Transactional(readOnly = true)
    public List<PatientSearchResultDto> searchByMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            return List.of();
        }
        return patientRepository.findAllByMobile(mobile.trim()).stream()
                .map(patient -> PatientSearchResultDto.builder()
                        .exists(true)
                        .patientId(patient.getId())
                        .patientCode(patient.getPatientCode())
                        .name(patient.getName())
                        .historyUrl("/doctor/patient/history/" + patient.getId())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientSearchResultDto> searchByMobileOrName(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.trim();
        return patientRepository.findByMobileContainingOrNameContainingAllIgnoreCase(q, q).stream()
                .map(patient -> PatientSearchResultDto.builder()
                        .exists(true)
                        .patientId(patient.getId())
                        .patientCode(patient.getPatientCode())
                        .name(patient.getName())
                        .historyUrl("/doctor/patient/history/" + patient.getId())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientSearchResultDto> searchByPatientCode(String patientCode) {
        if (patientCode == null || patientCode.isBlank()) {
            return Optional.empty();
        }
        return patientRepository.findByPatientCodeIgnoreCase(patientCode.trim())
                .map(patient -> PatientSearchResultDto.builder()
                        .exists(true)
                        .patientId(patient.getId())
                        .patientCode(patient.getPatientCode())
                        .name(patient.getName())
                        .historyUrl("/doctor/patient/history/" + patient.getId())
                        .build());
    }

    @Override
    @Transactional
    public Patient registerPatient(PatientCreateDto dto) {
        String mobile = dto.getMobile().trim();

        Patient patient = new Patient();
        patient.setPatientCode(idGenerationService.generatePatientId());
        patient.setName(dto.getName().trim());
        patient.setMobile(mobile);
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender().trim());

        // Vitals
        patient.setHeight(dto.getHeightCm() != null ? dto.getHeightCm().toString() : null);
        patient.setWeight(dto.getWeightKg() != null ? dto.getWeightKg().toString() : null);
        patient.setTemperature(trimOrNull(dto.getTemperature()));
        patient.setBloodPressure(trimOrNull(dto.getBloodPressure()));
        patient.setPulseRate(trimOrNull(dto.getPulseRate()));
        patient.setCbg(trimOrNull(dto.getCbg()));
        patient.setSpO2(dto.getSpo2() != null ? dto.getSpo2().toString() : null);

        // Legacy
        patient.setBloodSugar(trimOrNull(dto.getBloodSugar()));
        patient.setCreatedDate(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientHistoryDto getPatientHistory(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        List<Prescription> visits = prescriptionRepository
                .findByPatientIdWithDetailsOrderByDateCreatedDesc(patientId);

        return PatientHistoryDto.builder()
                .patient(patient)
                .lastVisit(visits.isEmpty() ? null : visits.getFirst())
                .visitHistory(visits)
                .previousPdfs(pdfRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Integer id) {
        return patientRepository.findById(id);
    }

    private static String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
