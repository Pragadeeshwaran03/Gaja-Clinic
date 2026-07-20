package com.gaja.clinic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BillCreateDto {

    @NotNull(message = "Prescription ID is required")
    private Integer prescriptionId;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", message = "Consultation fee must be non-negative")
    private BigDecimal consultationFee = BigDecimal.ZERO;

    @NotNull(message = "Other charges must be provided")
    @DecimalMin(value = "0.0", message = "Other charges must be non-negative")
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Size(max = 200, message = "Description must be at most 200 characters")
    private String otherChargesDescription;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    private BigDecimal discount = BigDecimal.ZERO;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(Cash|UPI)$", message = "Payment method must be Cash or UPI")
    private String paymentMethod = "Cash";

    private String utrNo;
}
