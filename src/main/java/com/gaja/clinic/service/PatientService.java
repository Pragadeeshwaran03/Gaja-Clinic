package com.gaja.clinic.service;

import com.gaja.clinic.dto.PatientCreateDto;
import com.gaja.clinic.dto.PatientHistoryDto;
import com.gaja.clinic.dto.PatientSearchResultDto;
import com.gaja.clinic.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    List<PatientSearchResultDto> searchByMobile(String mobile);

    List<PatientSearchResultDto> searchByMobileOrName(String query);

    Optional<PatientSearchResultDto> searchByPatientCode(String patientCode);

    Patient registerPatient(PatientCreateDto dto);

    PatientHistoryDto getPatientHistory(Integer patientId);

    Optional<Patient> findById(Integer id);
}
