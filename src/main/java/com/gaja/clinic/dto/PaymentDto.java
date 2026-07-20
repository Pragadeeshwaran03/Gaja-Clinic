package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentDto {

    private final String paymentMethod;
    private final String paymentStatus;
    private final String upiUtrNo;
    private final LocalDateTime paidDate;
    private final String staffName;
}
