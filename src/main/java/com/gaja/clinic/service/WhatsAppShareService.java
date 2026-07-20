package com.gaja.clinic.service;

import com.gaja.clinic.dto.WhatsAppShareInfo;
import com.gaja.clinic.dto.WhatsAppShareResult;

public interface WhatsAppShareService {

    WhatsAppShareResult buildShareUrl(WhatsAppShareInfo info);
}
