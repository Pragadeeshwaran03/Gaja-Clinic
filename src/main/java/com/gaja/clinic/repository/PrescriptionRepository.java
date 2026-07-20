package com.gaja.clinic.repository;

import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.entity.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);

    List<Prescription> findByStatus(PrescriptionStatus status);

    List<Prescription> findByStatusOrderByDateCreatedDesc(PrescriptionStatus status);

    List<Prescription> findByPatientIdOrderByDateCreatedDesc(Integer patientId);

    Optional<Prescription> findByIdAndStatus(Integer id, PrescriptionStatus status);

    Optional<Prescription> findTopByOrderByIdDesc();

    @Query("""
            SELECT p FROM Prescription p
            LEFT JOIN FETCH p.doctor d
            LEFT JOIN FETCH d.user
            LEFT JOIN FETCH p.items
            WHERE p.patientId = :patientId
            ORDER BY p.dateCreated DESC
            """)
    List<Prescription> findByPatientIdWithDetailsOrderByDateCreatedDesc(@Param("patientId") Integer patientId);

    @Query("""
            SELECT p FROM Prescription p
            LEFT JOIN FETCH p.items
            WHERE p.id = :id AND p.status = :status
            """)
    Optional<Prescription> findByIdAndStatusWithItems(@Param("id") Integer id,
                                                      @Param("status") PrescriptionStatus status);

    @Query("""
            SELECT p FROM Prescription p
            LEFT JOIN FETCH p.items
            LEFT JOIN FETCH p.patient
            WHERE p.id = :id
            """)
    Optional<Prescription> findByIdWithItemsAndPatient(@Param("id") Integer id);

    @Query("""
            SELECT p FROM Prescription p
            LEFT JOIN FETCH p.patient
            LEFT JOIN FETCH p.doctor d
            LEFT JOIN FETCH d.user
            WHERE p.status = :status
            ORDER BY p.dateCreated DESC
            """)
    List<Prescription> findByStatusWithPatientAndDoctorOrderByDateCreatedDesc(
            @Param("status") PrescriptionStatus status);

    @Query("""
            SELECT p FROM Prescription p
            LEFT JOIN FETCH p.items
            LEFT JOIN FETCH p.patient
            LEFT JOIN FETCH p.doctor d
            LEFT JOIN FETCH d.user
            WHERE p.id = :id AND p.status = :status
            """)
    Optional<Prescription> findPendingDetailsById(@Param("id") Integer id,
                                                  @Param("status") PrescriptionStatus status);

    @Query("""
            SELECT p FROM Prescription p
            LEFT JOIN FETCH p.items
            LEFT JOIN FETCH p.patient
            LEFT JOIN FETCH p.doctor d
            LEFT JOIN FETCH d.user
            WHERE p.id = :id
            """)
    Optional<Prescription> findByIdWithFullDetails(@Param("id") Integer id);

    List<Prescription> findTop5ByOrderByDateCreatedDesc();
}
