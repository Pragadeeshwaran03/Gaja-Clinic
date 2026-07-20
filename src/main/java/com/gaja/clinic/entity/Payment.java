package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "BillId", nullable = false, unique = true)
    private Integer billId;

    @Column(name = "PaymentMethod", nullable = false, length = 20)
    private String paymentMethod = "Cash";

    @Column(name = "PaymentStatus", nullable = false, length = 20)
    private String paymentStatus = "Unpaid";

    @Column(name = "UpiUtrNo", length = 100)
    private String upiUtrNo;

    @Column(name = "PaidDate")
    private LocalDateTime paidDate;

    @Column(name = "StaffName", length = 150)
    private String staffName;

    @Column(name = "CreatedDate", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BillId", insertable = false, updatable = false)
    private Bill bill;
}
