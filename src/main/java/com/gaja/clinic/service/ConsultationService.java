package com.gaja.clinic.service;

import com.gaja.clinic.entity.Prescription;

public interface ConsultationService {

    Prescription startConsultation(Integer patientId, Integer doctorUserId);
}
