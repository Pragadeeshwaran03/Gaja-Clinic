package com.gaja.clinic.repository;

import com.gaja.clinic.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Integer> {

    Optional<Bill> findTopByOrderByIdDesc();

    Optional<Bill> findByBillNo(String billNo);

    Optional<Bill> findByPrescriptionId(Integer prescriptionId);

    List<Bill> findByPatientId(Integer patientId);

    List<Bill> findByDoctorId(Integer doctorId);

    @Query("""
            SELECT b FROM Bill b
            LEFT JOIN FETCH b.patient
            LEFT JOIN FETCH b.prescription
            LEFT JOIN FETCH b.doctor d
            LEFT JOIN FETCH d.user
            LEFT JOIN FETCH b.payment
            LEFT JOIN FETCH b.pdfRecord
            WHERE b.id = :id
            """)
    Optional<Bill> findByIdWithDetails(@Param("id") Integer id);

    List<Bill> findTop5ByOrderByCreatedDateDesc();

    @Query("""
            SELECT COALESCE(SUM(b.finalAmount), 0) FROM Bill b
            WHERE b.createdDate >= :start AND b.createdDate < :end
            """)
    BigDecimal sumFinalAmountBetween(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}
