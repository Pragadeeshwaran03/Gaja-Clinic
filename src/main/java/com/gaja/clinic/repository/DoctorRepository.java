package com.gaja.clinic.repository;

import com.gaja.clinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findByUserId(Integer userId);

    long countByIsActiveTrue();
}
