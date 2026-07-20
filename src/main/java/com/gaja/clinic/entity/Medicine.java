package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Medicines")
@Getter
@Setter
@NoArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", nullable = false, length = 200)
    private String name;

    @Column(name = "GenericName", length = 200)
    private String genericName;

    @Column(name = "Category", length = 100)
    private String category;

    @Column(name = "Manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "Price", nullable = false, precision = 65, scale = 30)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "Stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "Unit", length = 50)
    private String unit;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CreatedDate", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
}
