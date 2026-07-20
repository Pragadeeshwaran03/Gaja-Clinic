package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrescriptionSaveResponse {

    private final boolean success;
    private final String message;
    private final String redirectUrl;

    public static PrescriptionSaveResponse ok(String redirectUrl) {
        return PrescriptionSaveResponse.builder()
                .success(true)
                .redirectUrl(redirectUrl)
                .build();
    }

    public static PrescriptionSaveResponse error(String message) {
        return PrescriptionSaveResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
