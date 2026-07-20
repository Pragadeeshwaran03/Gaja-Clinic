package com.gaja.clinic.service.impl;

import com.gaja.clinic.dto.PaymentDto;
import com.gaja.clinic.entity.Payment;
import com.gaja.clinic.repository.PaymentRepository;
import com.gaja.clinic.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment createPaidPayment(Integer billId, String paymentMethod, String utrNo, String staffName) {
        Payment payment = new Payment();
        payment.setBillId(billId);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("Paid");
        payment.setUpiUtrNo("UPI".equals(paymentMethod) ? utrNo : null);
        payment.setPaidDate(LocalDateTime.now());
        payment.setStaffName(staffName);
        payment.setCreatedDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentDto.builder()
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .upiUtrNo(payment.getUpiUtrNo())
                .paidDate(payment.getPaidDate())
                .staffName(payment.getStaffName())
                .build();
    }
}
