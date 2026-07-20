package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "PrescriptionItems")
@Getter
@Setter
@NoArgsConstructor
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "PrescriptionId", nullable = false)
    private Integer prescriptionId;

    @Column(name = "MedicineName", nullable = false, length = 200)
    private String medicineName;

    @Column(name = "Dosage", length = 100)
    private String dosage;

    @Column(name = "Duration", length = 100)
    private String duration;

    @Column(name = "FoodTiming", length = 100)
    private String foodTiming;

    @Column(name = "Instructions", length = 500)
    private String instructions;


    @Column(name = "Quantity", nullable = false)
    private Integer quantity = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PrescriptionId", insertable = false, updatable = false)
    private Prescription prescription;
}
