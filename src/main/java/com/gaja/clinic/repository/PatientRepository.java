package com.gaja.clinic.repository;

import com.gaja.clinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    List<Patient> findAllByMobile(String mobile);

    Optional<Patient> findByPatientCodeIgnoreCase(String patientCode);

    List<Patient> findByMobileContainingOrNameContainingAllIgnoreCase(String mobile, String name);

    Optional<Patient> findTopByOrderByIdDesc();

    List<Patient> findTop5ByOrderByCreatedDateDesc();
}
