package com.gaja.clinic.repository;

import com.gaja.clinic.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Integer> {

    List<PrescriptionItem> findByPrescriptionId(Integer prescriptionId);
}
