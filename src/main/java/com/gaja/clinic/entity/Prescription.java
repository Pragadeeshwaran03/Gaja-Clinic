package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Prescriptions")
@Getter
@Setter
@NoArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "PrescriptionNumber", nullable = false, columnDefinition = "LONGTEXT")
    private String prescriptionNumber;

    @Column(name = "PatientId", nullable = false)
    private Integer patientId;

    @Column(name = "DoctorId", nullable = false)
    private Integer doctorId;

    @Column(name = "DateCreated", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Status", nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.PENDING_PHARMACY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId", insertable = false, updatable = false)
    private Doctor doctor;

    @Column(name = "Notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "PatientCondition", columnDefinition = "TEXT")
    private String patientCondition;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> items = new ArrayList<>();
}
