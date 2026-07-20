package com.gaja.clinic.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WhatsAppShareResult {

    private final boolean success;
    private final String shareUrl;
    private final String errorMessage;

    public static WhatsAppShareResult ok(String shareUrl) {
        return WhatsAppShareResult.builder().success(true).shareUrl(shareUrl).build();
    }

    public static WhatsAppShareResult fail(String errorMessage) {
        return WhatsAppShareResult.builder().success(false).errorMessage(errorMessage).build();
    }
}
