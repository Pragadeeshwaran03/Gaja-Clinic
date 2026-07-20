package com.gaja.clinic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillItemDto {

    private Integer prescriptionItemId;
    private String medicineName;
    private String dosage;
    private int quantity;
    private String duration;
    private String instructions;
}
