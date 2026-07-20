package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PdfRecords")
@Getter
@Setter
@NoArgsConstructor
public class PdfRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "PatientId", nullable = false)
    private Integer patientId;

    @Column(name = "DoctorId", nullable = false)
    private Integer doctorId;

    @Column(name = "BillId", nullable = false, unique = true)
    private Integer billId;

    @Column(name = "PrescriptionId", nullable = false)
    private Integer prescriptionId;

    @Column(name = "FileName", nullable = false, length = 300)
    private String fileName;

    @Column(name = "FilePath", nullable = false, length = 500)
    private String filePath;

    @Column(name = "VisitDate", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "CreatedDate", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId", insertable = false, updatable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BillId", insertable = false, updatable = false)
    private Bill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PrescriptionId", insertable = false, updatable = false)
    private Prescription prescription;
}
