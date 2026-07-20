package com.gaja.clinic.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrescriptionItemDto {

    @NotBlank(message = "Medicine name is required")
    @Size(max = 200, message = "Medicine name must be at most 200 characters")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage must be at most 100 characters")
    private String dosage;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    @NotBlank(message = "Duration is required")
    @Size(max = 100, message = "Duration must be at most 100 characters")
    private String duration;

    @Size(max = 500, message = "Instructions must be at most 500 characters")
    private String instructions = "";
}
