package com.gaja.clinic.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCreateDto {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must be at most 150 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Size(max = 20, message = "Mobile must be at most 20 characters")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "Enter a valid mobile number")
    private String mobile;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be 0 or greater")
    @Max(value = 150, message = "Age must be 150 or less")
    private Integer age;

    @NotBlank(message = "Gender is required")
    @Size(max = 10, message = "Gender must be at most 10 characters")
    private String gender;

    // ── Vitals ─────────────────────────────────────────────────────────────

    @DecimalMin(value = "0.0", inclusive = false, message = "Height must be positive")
    @DecimalMax(value = "300.0", message = "Height must be realistic (≤ 300 cm)")
    private Double heightCm;

    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be positive")
    @DecimalMax(value = "500.0", message = "Weight must be realistic (≤ 500 kg)")
    private Double weightKg;

    @Size(max = 20, message = "Temperature must be at most 20 characters")
    private String temperature;

    @Size(max = 20, message = "Blood pressure must be at most 20 characters")
    @Pattern(regexp = "^(\\d{2,3}/\\d{2,3})?$", message = "Blood pressure must be in format 120/80")
    private String bloodPressure;

    @Size(max = 20, message = "Pulse rate must be at most 20 characters")
    @Pattern(regexp = "^(\\d+)?$", message = "Pulse rate must be a positive number")
    private String pulseRate;

    @Size(max = 20, message = "CBG must be at most 20 characters")
    private String cbg;

    @DecimalMin(value = "0.0", message = "SpO2 must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "SpO2 must be between 0 and 100")
    private Double spo2;

    // ── Legacy field kept for backward compatibility ────────────────────────
    @Size(max = 20, message = "Blood sugar must be at most 20 characters")
    private String bloodSugar;
}
