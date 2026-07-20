package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Patients")
@Getter
@Setter
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "PatientCode", nullable = false, length = 20)
    private String patientCode;

    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Column(name = "Mobile", nullable = false, length = 20)
    private String mobile;

    @Column(name = "Age", nullable = false)
    private Integer age;

    @Column(name = "Gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "BloodGroup", length = 10)
    private String bloodGroup;

    @Column(name = "Height", length = 20)
    private String height;

    @Column(name = "Weight", length = 20)
    private String weight;

    @Column(name = "BloodPressure", length = 20)
    private String bloodPressure;

    @Column(name = "BloodSugar", length = 20)
    private String bloodSugar;

    @Column(name = "Temperature", length = 20)
    private String temperature;

    @Column(name = "PulseRate", length = 20)
    private String pulseRate;

    @Column(name = "SpO2", length = 20)
    private String spO2;

    @Column(name = "CBG", length = 20)
    private String cbg;

    @Column(name = "Address", length = 500)
    private String address;

    @Column(name = "Allergies", length = 500)
    private String allergies;

    @Column(name = "ExistingDiseases", length = 500)
    private String existingDiseases;

    @Column(name = "Notes", length = 1000)
    private String notes;

    @Column(name = "CreatedDate", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "UpdatedDate")
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "patient")
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    private List<Bill> bills = new ArrayList<>();
}
