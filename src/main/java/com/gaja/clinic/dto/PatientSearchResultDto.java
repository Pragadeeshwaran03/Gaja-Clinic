package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientSearchResultDto {

    private final boolean exists;
    private final Integer patientId;
    private final String patientCode;
    private final String name;
    private final String historyUrl;
}
