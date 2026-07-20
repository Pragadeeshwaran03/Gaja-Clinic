package com.gaja.clinic.repository;

import com.gaja.clinic.entity.PdfRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PdfRecordRepository extends JpaRepository<PdfRecord, Integer>, JpaSpecificationExecutor<PdfRecord> {

    Optional<PdfRecord> findByBillId(Integer billId);

    List<PdfRecord> findByPatientIdOrderByVisitDateDesc(Integer patientId);

    List<PdfRecord> findAllByOrderByCreatedDateDesc();

    @Query("""
            SELECT p FROM PdfRecord p
            LEFT JOIN FETCH p.patient
            LEFT JOIN FETCH p.doctor d
            LEFT JOIN FETCH d.user
            LEFT JOIN FETCH p.bill
            WHERE p.id = :id
            """)
    Optional<PdfRecord> findByIdWithDetails(@Param("id") Integer id);
}
