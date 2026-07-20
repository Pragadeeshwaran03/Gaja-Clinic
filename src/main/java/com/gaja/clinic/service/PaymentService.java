package com.gaja.clinic.service;

import com.gaja.clinic.dto.PaymentDto;
import com.gaja.clinic.entity.Payment;

public interface PaymentService {

    Payment createPaidPayment(Integer billId, String paymentMethod, String utrNo, String staffName);

    PaymentDto toDto(Payment payment);
}
