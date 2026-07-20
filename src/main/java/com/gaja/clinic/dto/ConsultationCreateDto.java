package com.gaja.clinic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultationCreateDto {

    @NotNull(message = "Patient is required")
    private Integer patientId;

    private String patientName;
}
