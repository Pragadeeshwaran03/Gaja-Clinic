package com.gaja.clinic.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PrescriptionCreateDto {

    @NotNull(message = "Prescription ID is required")
    private Integer prescriptionId;

    @Size(max = 2000, message = "Patient condition must be at most 2000 characters")
    private String patientCondition;

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    private String notes;

    @Valid
    private List<PrescriptionItemDto> items = new ArrayList<>();
}
