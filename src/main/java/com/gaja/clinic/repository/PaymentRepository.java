package com.gaja.clinic.repository;

import com.gaja.clinic.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByBillId(Integer billId);
}
