package com.gaja.clinic.service;

import com.gaja.clinic.dto.PrescriptionCreateDto;
import com.gaja.clinic.dto.PrescriptionSaveResponse;
import com.gaja.clinic.entity.Prescription;

public interface PrescriptionService {

    Prescription loadForEditing(Integer prescriptionId);

    PrescriptionSaveResponse saveItems(PrescriptionCreateDto dto);

    Prescription loadWithDetails(Integer prescriptionId);
}
