package com.gaja.clinic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Settings")
@Getter
@Setter
@NoArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Key", nullable = false, length = 100)
    private String key;

    @Column(name = "Value", length = 1000)
    private String value;

    @Column(name = "Description", length = 200)
    private String description;
}
