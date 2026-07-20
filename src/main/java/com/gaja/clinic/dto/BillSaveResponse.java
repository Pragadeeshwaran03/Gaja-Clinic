package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillSaveResponse {

    private final boolean success;
    private final String message;
    private final String redirectUrl;

    public static BillSaveResponse ok(String redirectUrl) {
        return BillSaveResponse.builder().success(true).redirectUrl(redirectUrl).build();
    }

    public static BillSaveResponse error(String message) {
        return BillSaveResponse.builder().success(false).message(message).build();
    }
}
