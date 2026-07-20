package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Doctors")
@Getter
@Setter
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "UserId", nullable = false)
    private Integer userId;

    @Column(name = "Specialization", length = 200)
    private String specialization;

    @Column(name = "Qualification", length = 200)
    private String qualification;

    @Column(name = "LicenseNo", length = 100)
    private String licenseNo;

    @Column(name = "About", length = 500)
    private String about;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CreatedDate", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "doctor")
    private List<Prescription> prescriptions = new ArrayList<>();
}
