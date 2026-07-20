package com.gaja.clinic.dto;

import com.gaja.clinic.entity.Patient;
import com.gaja.clinic.entity.PdfRecord;
import com.gaja.clinic.entity.Prescription;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PatientHistoryDto {

    private final Patient patient;
    private final Prescription lastVisit;
    private final List<Prescription> visitHistory;
    private final List<PdfRecord> previousPdfs;
}
